/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason AgentSpeak(L++)                                #
 * # Copyright (c) 2015-16, LightJason (info@lightjason.org)                            #
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

package org.lightjason.agentspeak.action.buildin.math.blas.vector;

import cern.colt.matrix.DoubleMatrix1D;
import org.lightjason.agentspeak.action.buildin.IBuildinAction;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.util.List;
import java.util.stream.IntStream;


/**
 * assigns a value or matrix to all elements
 */
public final class CAssign extends IBuildinAction
{

    /**
     * ctor
     */
    public CAssign()
    {
        super( 4 );
    }

    @Override
    public final int minimalArgumentNumber()
    {
        return 2;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        // first argument must be a term with a matrix object, second assign value
        final DoubleMatrix1D l_vector = p_argument.get( 0 ).toAny();
        final Object l_value = p_argument.get( 1 ).toAny();

        if ( l_value instanceof Number )
        {
            p_return.add( CRawTerm.from( l_vector.assign( ( (Number) l_value ).doubleValue() ) ) );
            return CFuzzyValue.from( true );
        }

        if ( l_value instanceof DoubleMatrix1D )
        {
            p_return.add( CRawTerm.from( l_vector.assign( (DoubleMatrix1D) l_value ) ) );
            return CFuzzyValue.from( true );
        }

        if ( l_value instanceof List<?> )
        {
            final List<Double> l_data = (List<Double>) l_value;
            IntStream.range( 0, Math.min( l_vector.size(), l_data.size() ) ).boxed().forEach( i -> l_vector.setQuick( i, l_data.get( i ) ) );
            return CFuzzyValue.from( true );
        }

        return CFuzzyValue.from( false );
    }
}
