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

package org.lightjason.agentspeak.language.variable;

import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.error.CIllegalStateException;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * structure for creating evaluate variables
 */
public final class CVariableEvaluate implements IVariableEvaluate
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 7310663182659231951L;
    /**
     * content variable with a string or literal
     */
    private final IVariable<?> m_variable;
    /**
     * optional parameter list
     */
    private final List<ITerm> m_parameter;

    /**
     * ctor
     *
     * @param p_variable variable
     */
    public CVariableEvaluate( @Nonnull final IVariable<?> p_variable )
    {
        this( p_variable, Collections.<ITerm>emptyList() );
    }

    /**
     * ctor
     *
     * @param p_variable variable
     * @param p_parameter optional parameter list
     */
    public CVariableEvaluate( @Nonnull final IVariable<?> p_variable, @Nonnull final List<ITerm> p_parameter )
    {
        m_variable = p_variable;
        m_parameter = Collections.unmodifiableList( p_parameter );
    }


    @Override
    public final boolean mutex()
    {
        return m_variable.mutex();
    }

    @Nonnull
    @Override
    public final ILiteral evaluate( final IContext p_context )
    {
        final IVariable<?> l_variable = CCommon.replaceFromContext( p_context, m_variable ).term();
        if ( !l_variable.allocated() )
            throw new CIllegalStateException();

        // if variable is a string
        if ( l_variable.valueassignableto( String.class ) )
            return this.fromString( l_variable.raw(), p_context );

        if ( m_variable.valueassignableto( ILiteral.class ) )
            return this.fromLiteral( l_variable.raw(), p_context );

        throw new CIllegalStateException();
    }

    @Nonnull
    @Override
    public Stream<IVariable<?>> variables()
    {
        return Stream.concat(
            Stream.of( m_variable ),
            m_parameter.parallelStream()
                       .filter( i -> i instanceof IVariable<?> )
                       .map( ITerm::term )
        );
    }


    @Override
    public final int hashCode()
    {
        return m_variable.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( p_object instanceof IVariableEvaluate ) && ( this.hashCode() == p_object.hashCode() );
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "{0}{1}", m_variable, m_parameter.isEmpty() ? "" : m_parameter );
    }

    @Nonnull
    @Override
    public final String functor()
    {
        return m_variable.functor();
    }

    @Nonnull
    @Override
    public final IPath functorpath()
    {
        return m_variable.functorpath();
    }

    @Nonnull
    @Override
    public final IPath fqnfunctor()
    {
        return m_variable.fqnfunctor();
    }

    @Override
    public final boolean hasVariable()
    {
        return true;
    }

    @Override
    public final <T> T raw()
    {
        return m_variable.raw();
    }

    @Nonnull
    @Override
    public final ITerm deepcopy( final IPath... p_prefix )
    {
        return m_variable.deepcopy( p_prefix );
    }

    @Nonnull
    @Override
    public final ITerm deepcopysuffix()
    {
        return m_variable.deepcopysuffix();
    }

    @Override
    public final int structurehash()
    {
        return 0;
    }

    /**
     * creates the result literal from an input string
     *
     * @param p_value input string (literal functor)
     * @param p_context execution context
     * @return result literal
     */
    private ILiteral fromString( final String p_value, final IContext p_context )
    {
        return CLiteral.from( p_value, m_parameter ).unify( p_context );
    }

    /**
     * creates the result literal from an input literal
     *
     * @param p_literal input literal
     * @param p_context execution context
     * @return result literal
     */
    private ILiteral fromLiteral( final ILiteral p_literal, final IContext p_context )
    {
        return m_parameter.isEmpty()
            ? p_literal.unify( p_context )
            : new CLiteral(
                p_literal.hasAt(),
                p_literal.negated(),
                p_literal.fqnfunctor(),
                m_parameter
            ).unify( p_context );
    }

}
