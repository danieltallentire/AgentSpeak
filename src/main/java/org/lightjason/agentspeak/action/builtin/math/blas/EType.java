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

package org.lightjason.agentspeak.action.builtin.math.blas;


import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * blas types
 */
public enum EType
{
    SPARSE,
    DENSE;

    /**
     * set with names
     */
    private static final Set<String> NAMES = Collections.unmodifiableSet( Arrays.stream( EType.values() ).map( i -> i.name() ).collect( Collectors.toSet() ) );

    /**
     * additional factory
     *
     * @param p_name name as string
     * @return enum
     */
    public static EType from( final String p_name )
    {
        return EType.valueOf( p_name.trim().toUpperCase( Locale.ROOT ) );
    }

    /**
     * check method to check if a enum value with a name exists
     *
     * @param p_name name as string
     * @return boolean if enum value exists
     */
    public static boolean exists( final String p_name )
    {
        return NAMES.contains( p_name.trim().toUpperCase( Locale.ROOT ) );
    }

}
