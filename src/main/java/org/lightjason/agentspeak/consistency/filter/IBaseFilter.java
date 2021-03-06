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

package org.lightjason.agentspeak.consistency.filter;

import org.lightjason.agentspeak.common.IPath;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * default metric with an optional set of path values
 */
abstract class IBaseFilter implements IFilter
{
    /**
     * set with paths
     */
    protected final Set<IPath> m_paths = new HashSet<>();

    /**
     * ctor
     *
     * @param p_paths for reading agent value
     */
    protected IBaseFilter( final IPath... p_paths )
    {
        if ( p_paths != null )
            m_paths.addAll( Arrays.asList( p_paths ) );
    }

    /**
     * ctor
     *
     * @param p_paths collection of path
     */
    protected IBaseFilter( final Collection<IPath> p_paths )
    {
        if ( p_paths != null )
            m_paths.addAll( p_paths );
    }

    /**
     * returns the selectors
     *
     * @return selector
     */
    public final Collection<IPath> getSelector()
    {
        return m_paths;
    }
}
