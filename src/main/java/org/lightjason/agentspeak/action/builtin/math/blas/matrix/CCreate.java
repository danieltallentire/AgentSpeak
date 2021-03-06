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

package org.lightjason.agentspeak.action.builtin.math.blas.matrix;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import com.codepoetics.protonpack.StreamUtils;
import org.lightjason.agentspeak.action.builtin.IBuiltinAction;
import org.lightjason.agentspeak.action.builtin.math.blas.EType;
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
 * creates a dense- or sparse-matrix.
 * The action creates densore or sparse matrix objects, the
 * last object is a string with dense or sparse, all other
 * arguments are tuples of row and column size.
 *
 * @code [M1|M2] = math/blas/matrix/create(2,2, [3,2], "dense|sparse"); @endcode
 */
public final class CCreate extends IBuiltinAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 6354092233793492378L;

    /**
     * ctor
     */
    public CCreate()
    {
        super( 4 );
    }

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
        final List<ITerm> l_arguments = CCommon.flatten( p_argument ).collect( Collectors.toList() );
        final int l_limit;
        final EType l_type;
        if ( ( CCommon.rawvalueAssignableTo( l_arguments.get( l_arguments.size() - 1 ), String.class ) )
             && ( EType.exists( l_arguments.get( l_arguments.size() - 1 ).<String>raw() ) ) )
        {
            l_type = EType.from( l_arguments.get( l_arguments.size() - 1 ).<String>raw() );
            l_limit = l_arguments.size() - 1;
        }
        else
        {
            l_type = EType.DENSE;
            l_limit = l_arguments.size();
        }


        // create matrix
        switch ( l_type )
        {
            case DENSE:
                StreamUtils.windowed(
                    l_arguments.stream()
                               .limit( l_limit )
                               .map( ITerm::<Number>raw )
                               .mapToInt( Number::intValue )
                               .boxed(),
                    2
                )
                           .map( i -> new DenseDoubleMatrix2D( i.get( 0 ), i.get( 1 ) ) )
                           .map( CRawTerm::from )
                           .forEach( p_return::add );

                return CFuzzyValue.from( true );


            case SPARSE:
                StreamUtils.windowed(
                    l_arguments.stream()
                               .limit( l_limit )
                               .map( ITerm::<Number>raw )
                               .mapToInt( Number::intValue )
                               .boxed(),
                    2
                )
                           .map( i -> new SparseDoubleMatrix2D( i.get( 0 ), i.get( 1 ) ) )
                           .map( CRawTerm::from )
                           .forEach( p_return::add );

                return CFuzzyValue.from( true );


            default:
                return CFuzzyValue.from( false );
        }
    }

}
