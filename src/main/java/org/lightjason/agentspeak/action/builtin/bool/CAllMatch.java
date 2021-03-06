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

package org.lightjason.agentspeak.action.builtin.bool;

import org.lightjason.agentspeak.action.builtin.IBuiltinAction;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;


/**
 * checks all elements are equal to the first argument.
 * The actions checks the first argument to all other if this
 * matchs for equality, the action never fails
 *
 * @code AllEqual = bool/allmatch( "this is the test", 123, "this is the test", ["hello", 234] ); @endcode
 * @note on number arguments not the value must equal, also the type (double / integral) must be equal,
 * so keep in mind, that you use the correct number type on the argument input
 */
public final class CAllMatch extends IBuiltinAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = -5787849046607404490L;

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
        final List<?> l_arguments = CCommon.flatten( p_argument ).map( ITerm::raw ).collect( Collectors.toList() );

        p_return.add(
            CRawTerm.from(
                l_arguments.stream()
                           .skip( 1 )
                           .allMatch( i -> l_arguments.get( 0 ).equals( i ) )
            )
        );

        return CFuzzyValue.from( true );
    }

}
