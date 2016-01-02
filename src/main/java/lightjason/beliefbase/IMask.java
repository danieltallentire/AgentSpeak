/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the Light-Jason                                               #
 * # Copyright (c) 2015, Philipp Kraus (philipp.kraus@tu-clausthal.de)                  #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package lightjason.beliefbase;


import com.google.common.collect.SetMultimap;
import lightjason.common.CPath;
import lightjason.language.ILiteral;

import java.util.Map;
import java.util.Set;


/**
 * mask of the path
 */
public interface IMask extends IStructure
{

    /**
     * adds a mask in the current structure
     *
     * @param p_path path
     * @param p_mask mask
     * @note the mask that is put in the method will be cloned, so the returned mask are not equal, the parameter is a template object only
     */
    IMask add( final CPath p_path, final IMask p_mask );

    /**
     * adds a mask in the current structure
     *
     * @param p_path path
     * @param p_generator beliefbase generator if beliefbase not exists
     * @note the mask that is put in the method will be cloned, so the returned mask are not equal, the parameter is a template object only
     * @returns returns the added mask
     */
    IMask add( final CPath p_path, final IGenerator<Object> p_generator );

    /**
     * adds a mask in the current structure
     *
     * @param p_path path
     * @param p_mask mask
     * @param p_generator beliefbase generator if beliefbase not exists
     * @note the mask that is put in the method will be cloned, so the returned mask are not equal, the parameter is a template object only
     * @returns returns the added mask
     */
    IMask add( final CPath p_path, final IMask p_mask, final IGenerator<Object> p_generator );


    /**
     * adds a literal in the current structure
     *
     * @param p_literal literal
     * @param p_generator beliefbase generator if beliefbase not exists
     */
    void add( final ILiteral p_literal, final IGenerator<Object> p_generator );


    /**
     * checks if a mask exists
     *
     * @param p_path path to a mask (suffix is mask name)
     * @return existance boolean
     */
    boolean containsMask( final CPath p_path );

    /**
     * checks if a literal exists
     *
     * @param p_path path to a literal (suffix is literal name)
     * @return existance boolean
     */
    boolean containsLiteral( final CPath p_path );

    /**
     * removes a literal
     *
     * @param p_literal literal
     * @return is found and removed
     */
    boolean remove( final ILiteral p_literal );

    /**
     * removes a mask
     *
     * @param p_path path
     * @param p_mask mask
     * @return is found and removed
     */
    boolean remove( final CPath p_path, final IMask p_mask );

    /**
     * removes literal and mask
     *
     * @param p_path path
     * @return is found and removed
     */
    boolean remove( final CPath p_path );

    /**
     * clones the current mask
     *
     * @param p_parent new parent
     * @return new mask object
     */
    IMask clone( final IMask p_parent );

    /**
     * gets a list of all literals
     * of the path
     *
     * @param p_path path
     * @return map with literal
     */
    SetMultimap<CPath, ILiteral> getLiterals( final CPath p_path );

    /**
     * gets a list of all literals
     *
     * @return set with literals
     */
    SetMultimap<CPath, ILiteral> getLiterals();

    /**
     * returns a literal
     *
     * @param p_path path of the literal
     * @return set of literals or null
     */
    Set<ILiteral> getLiteral( final CPath p_path );

    /**
     * returns a mask
     *
     * @param p_path path of the mask
     * @return mask or null
     */
    IMask getMask( final CPath p_path );

    /**
     * gets a list of all literals
     * of the path
     *
     * @param p_path path
     * @return map with literal
     */
    Map<CPath, IMask> getMasks( final CPath p_path );

    /**
     * gets a list of all literals
     *
     * @return map with literals
     */
    Map<CPath, IMask> getMasks();

    /**
     * returns the full path
     *
     * @return path
     */
    CPath getFQNPath();

    /**
     * returns only the element name
     *
     * @return name
     */
    String getName();

    /**
     * returns the parent of the mask
     *
     * @return parent object or null
     */
    IMask getParent();

    /**
     * returns if the mask has a parent
     *
     * @return boolean flag of the parent
     */
    boolean hasParent();

    /**
     * interface for generating non-existing beliefbases
     */
    interface IGenerator<Q>
    {
        IMask createBeliefbase( final String p_name );
    }
}