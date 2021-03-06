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

package org.lightjason.agentspeak.action.builtin.agent;

import com.codepoetics.protonpack.StreamUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.lightjason.agentspeak.action.builtin.IBuiltinAction;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;


/**
 * removes a plan by the plan trigger.
 * The action moves all plan based on the
 * input trigger arguments, the action fails
 * on wrong input
 *
 * @code agent/removeplan( "+!", "myplan(X)", "-!", Literal ); @endcode
 */
@SuppressFBWarnings( "GC_UNRELATED_TYPES" )
public final class CRemovePlan extends IBuiltinAction
{

    /**
     * serial id
     */
    private static final long serialVersionUID = -2509947123811973880L;

    @Nonnegative
    @Override
    public final int minimalArgumentNumber()
    {
        return 1;
    }

    @Nonnull
    @Override
    public final IFuzzyValue<Boolean> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                               @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return
    )
    {
        return CFuzzyValue.from(
            StreamUtils.windowed(
                CCommon.flatten( p_argument ),
                2,
                2
            ).allMatch( i -> CRemovePlan.remove( ITrigger.EType.from( i.get( 0 ).<String>raw() ), i.get( 1 ), p_context.agent() ) )
        );
    }

    /**
     * removes the plan based on the trigger
     *
     * @param p_trigger trigger type
     * @param p_literal literal as string or literal object
     * @param p_agent agent
     * @return flag to remove plan successfully
     */
    private static boolean remove( @Nonnull final ITrigger.EType p_trigger, @Nonnull final ITerm p_literal, @Nonnull final IAgent<?> p_agent )
    {
        final ILiteral l_literal;
        try
        {

            l_literal = CCommon.rawvalueAssignableTo( p_literal, ILiteral.class )
                        ? p_literal.<ILiteral>raw()
                        : CLiteral.parse( p_literal.<String>raw() );

        }
        catch ( final Exception l_exception )
        {
            return false;
        }

        return !p_agent.plans().removeAll( CTrigger.from( p_trigger, l_literal ) ).isEmpty();
    }
}
