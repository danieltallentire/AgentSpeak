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

import edu.uci.ics.jung.graph.Graph;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * checks if a vertex and an edge incident for each graph instance.
 * The actions checks for the first vertex argument and the second
 * egde argument if both are incident on each graph instance, the
 * action never fails
 *
 * @code [B1|B2] = graph/isincidentsingle( Vertex, Edge, Graph1, Graph2 ); @endcode
 */
public final class CIsIncidentSingle extends IApplySingle
{

    @Override
    protected final int skipsize()
    {
        return 2;
    }

    @Override
    protected final void apply( final boolean p_parallel, @Nonnull final Graph<Object, Object> p_graph,
                                @Nonnull final List<ITerm> p_window, @Nonnull final List<ITerm> p_return )
    {
        p_return.add(
            CRawTerm.from(
                p_graph.isIncident( p_window.get( 0 ).raw(), p_window.get( 1 ).raw() )
            )
        );
    }

}
