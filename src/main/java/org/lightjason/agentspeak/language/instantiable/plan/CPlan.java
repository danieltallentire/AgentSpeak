/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason AgentSpeak(L++)                                #
 * # Copyright (c) 2015-17, LightJason (info@lightjason.org)                            #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package org.lightjason.agentspeak.language.instantiable.plan;

import org.apache.commons.lang3.StringUtils;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.IExecution;
import org.lightjason.agentspeak.language.execution.expression.IExpression;
import org.lightjason.agentspeak.language.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;
import org.lightjason.agentspeak.language.instantiable.IBaseInstantiable;
import org.lightjason.agentspeak.language.instantiable.plan.annotation.IAnnotation;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightjason.agentspeak.language.variable.IVariable;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


/**
 * plan structure
 */
public final class CPlan extends IBaseInstantiable implements IPlan
{
    /**
     * serial id
     */
    private static final long serialVersionUID = -8130277494195919583L;
    /**
     * trigger event
     */
    private final ITrigger m_triggerevent;
    /**
     * execution condition / expression
     */
    private final IExpression m_condition;


    /**
     * ctor
     *
     * @param p_event trigger event
     * @param p_body plan body
     * @param p_annotation annotations
     */
    public CPlan( @Nonnull final ITrigger p_event, @Nonnull final List<IExecution> p_body, @Nonnull final Set<IAnnotation<?>> p_annotation )
    {
        this( p_event, IExpression.EMPTY, p_body, p_annotation );
    }

    /**
     * ctor
     *
     * @param p_event trigger event
     * @param p_condition execution condition
     * @param p_body plan body
     * @param p_annotation annotations
     */
    public CPlan( @Nonnull final ITrigger p_event, @Nonnull final IExpression p_condition,
                  @Nonnull final List<IExecution> p_body, @Nonnull final Set<IAnnotation<?>> p_annotation )
    {
        super(
            p_body,
            p_annotation,

            CCommon.streamconcat(
                Stream.of(
                    p_event.hashCode(),
                    p_condition.hashCode()
                ),
                p_body.stream().map( Object::hashCode ),
                p_annotation.stream().map( Object::hashCode )
            ).reduce( 0, ( i, j ) -> i ^ j )
        );

        m_triggerevent = p_event;
        m_condition = p_condition;
    }

    @Nonnull
    @Override
    public final ITrigger trigger()
    {
        return m_triggerevent;
    }

    @Nonnull
    @Override
    public final IFuzzyValue<Boolean> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                               @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return
    )
    {
        final IFuzzyValue<Boolean> l_result = super.execute( p_parallel, p_context, p_argument, p_return );

        // create delete-goal trigger
        if ( !p_context.agent().fuzzy().getValue().defuzzify( l_result ) )
            p_context.agent().trigger( CTrigger.from( ITrigger.EType.DELETEGOAL, m_triggerevent.literal().unify( p_context ) ) );

        return l_result;
    }

    @Nonnull
    @Override
    public final IFuzzyValue<Boolean> condition( final IContext p_context )
    {
                /*
        // check condition for bounded variables
        if ( m_condition.variables()
                        .parallel()
                        .map( i -> CCommon.replaceFromContext( p_context, i ) )
                        .filter( ITerm::hasVariable )
                        .map( i -> (IVariable) i )
                        .anyMatch( i -> !i.allocated() )
            )
            return CFuzzyValue.from( false );

        */

        final List<ITerm> l_return = new LinkedList<>();
        return CFuzzyValue.from(
            m_condition.execute( false, p_context, Collections.emptyList(), l_return ).value()
            && ( l_return.size() == 1 )
            ? l_return.get( 0 ).<Boolean>raw()
            : false
        );
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format(
            "{0} ({1} | {2}{3} ==>> {4})",
            super.toString(),
            m_annotation.values(),
            m_triggerevent,
            m_condition == null ? "" : MessageFormat.format( " |- {0}", m_condition ),
            StringUtils.join( m_action, "; " )
        );
    }

    @Nonnull
    @Override
    public final Stream<IVariable<?>> variables()
    {
        return CCommon.streamconcat(
            m_condition != null ? m_condition.variables() : Stream.empty(),
            super.variables(),
            m_annotation.values().stream().flatMap( IAnnotation::variables ),
            CCommon.flattenrecursive( m_triggerevent.literal().orderedvalues() ).filter( i -> i instanceof IVariable<?> ).map( i -> (IVariable<?>) i )
        );
    }

}
