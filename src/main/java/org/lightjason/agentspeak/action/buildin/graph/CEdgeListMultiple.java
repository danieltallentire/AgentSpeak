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

package org.lightjason.agentspeak.action.buildin.graph;

import edu.uci.ics.jung.graph.AbstractGraph;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;

import java.util.ArrayList;
import java.util.List;


/**
 * returns all edges of all vertex tuples for a single graph instance.
 * The first argument is a graph instance and all other arguments are tuples
 * of vertices, for each tuple a list of all edges between the vertices is
 * returned, the action never fails
 *
 * @code [L1|L2] = graph/edgelist( Graph, Vertex1, Vertex2, [Vertex3, Vertex4] ); @endcode
 */
public final class CEdgeListMultiple extends IApplyMultiple
{
    @Override
    protected final int windowsize()
    {
        return 2;
    }

    @Override
    protected final void apply( final AbstractGraph<Object, Object> p_graph, final List<ITerm> p_window, final List<ITerm> p_return )
    {
        p_return.add(
            CRawTerm.from(
                new ArrayList<>( p_graph.findEdgeSet( p_window.get( 0 ).raw(), p_window.get( 1 ) .raw() ) )
            )
        );
    }
}
