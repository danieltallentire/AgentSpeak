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

package org.lightjason.agentspeak.action.builtin.string;

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
 * calculates the normalized-compression-distance.
 * The action calculates the normalized-compression-distance between
 * string, if the first argument matches a compression algorithm ( BZIP |
 * GZIP | DEFLATE | PACK200 | XZ ), it will be used for defining the compression,
 * the next string argument will be the input string and the distances will be
 * calculated between the second and all other arguments, the action fails on wrong input
 *
 * @code [A|B] = string/ncd( "BZIP|GZIP|DEFLATE|PACK200|XZ", "foo bar", "test foo", "bar foo" ); @endcode
 * @see https://en.wikipedia.org/wiki/Normalized_compression_distance
 */
public final class CNCD extends IBuiltinAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = -3828839577514827324L;

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
        final List<String> l_arguments = CCommon.flatten( p_argument )
                                                .map( ITerm::<String>raw )
                                                .collect( Collectors.toList() );

        // get arguments
        final int l_skip;
        final CCommon.ECompression l_compression;

        if ( ( !l_arguments.isEmpty() ) && ( CCommon.ECompression.exist( l_arguments.get( 0 ) ) ) )
        {
            l_compression = CCommon.ECompression.from( l_arguments.get( 0 ) );
            l_skip = 1;
        }
        else
        {
            l_compression = CCommon.ECompression.BZIP;
            l_skip = 0;
        }

        // check input arguments
        if ( l_arguments.size() < 2 + l_skip )
            return CFuzzyValue.from( false );


        // calculate distance
        l_arguments.stream()
                   .skip( l_skip + 1 )
                   .mapToDouble( i -> CCommon.ncd( l_compression, l_arguments.get( l_skip ), i ) )
                   .boxed()
                   .map( CRawTerm::from )
                   .forEach( p_return::add );

        return CFuzzyValue.from( true );
    }
}
