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

package org.lightjason.agentspeak.action.builtin.math;

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
import java.util.stream.Stream;


/**
 * action for geometric mean.
 * The action calculates \f$ \sqrt[i]{\prod_{i} x_i} \f$
 * over all unflatten arguments, action fails never
 *
 * @code G = math/geometricmean( 1, 3, 9, [10, [11, 12]] ); @endcode
 * @see https://en.wikipedia.org/wiki/Average
 */
public final class CGeometricMean extends IBuiltinAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = -3397904432745109682L;

    @Nonnegative
    @Override
    public final int minimalArgumentNumber()
    {
        return 1;
    }

    @Nonnull
    @Override
    public final IFuzzyValue<Boolean> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                               @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return )
    {
        p_return.add(
            CRawTerm.from(
                CGeometricMean.apply(
                    CCommon.flatten( p_argument ).count(),
                    CCommon.flatten( p_argument )
                           .map( ITerm::<Number>raw )
                           .mapToDouble( Number::doubleValue )
                           .boxed()
                )
            )
        );

        return CFuzzyValue.from( true );
    }


    /**
     * calculate geometric-mean
     *
     * @param p_count number of elements
     * @param p_values value stream
     * @return geometric mean
     */
    private static double apply( final double p_count, @Nonnull final Stream<Double> p_values )
    {
        return Math.pow(
            p_values.reduce( 1.0, ( i, j ) -> i * j ),
            1.0 / p_count
        );
    }

}
