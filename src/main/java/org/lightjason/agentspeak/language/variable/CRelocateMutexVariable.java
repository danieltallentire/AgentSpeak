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

package org.lightjason.agentspeak.language.variable;

import com.rits.cloning.Cloner;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.ITerm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * class for a mutex relocated variable
 *
 * @tparam T variable type
 */
public final class CRelocateMutexVariable<T> extends CMutexVariable<T> implements IRelocateVariable
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 6680660424006072619L;
    /**
     * reference to relocated variable
     */
    private final IVariable<?> m_relocate;


    /**
     * ctor
     *
     * @param p_variable variable which should be reloacted
     */
    public CRelocateMutexVariable( final IVariable<?> p_variable )
    {
        super( p_variable.functor(), p_variable.raw() );
        m_relocate = p_variable;
    }

    /**
     * ctor
     *  @param p_functor variable name
     * @param p_relocate variable which should be relocated
     */
    public CRelocateMutexVariable( @Nonnull final IPath p_functor, @Nonnull final IVariable<?> p_relocate )
    {
        super( p_functor, p_relocate.raw() );
        m_relocate = p_relocate;
    }

    /**
     * private ctor for creating object-copy
     *
     * @param p_functor functor
     * @param p_variable referenced variable
     * @param p_value value
     */
    private CRelocateMutexVariable( @Nonnull final IPath p_functor, @Nonnull final IVariable<?> p_variable, @Nullable final T p_value )
    {
        super( p_functor, p_value );
        m_relocate = p_variable;
    }

    @Nonnull
    @Override
    public final IVariable<?> relocate()
    {
        return m_relocate instanceof CConstant<?>
               ? m_relocate
               : m_relocate.set( this.raw() );
    }


    @Nonnull
    @Override
    public final IVariable<T> shallowcopy( @Nullable final IPath... p_prefix )
    {
        return ( p_prefix == null ) || ( p_prefix.length == 0 )
               ? new CRelocateMutexVariable<>( m_functor, m_relocate, m_value )
               : new CRelocateMutexVariable<>( p_prefix[0].append( m_functor ), m_relocate, m_value );
    }

    @Nonnull
    @Override
    public final ITerm deepcopysuffix()
    {
        return new CRelocateMutexVariable<>( CPath.from( m_functor.suffix() ), m_relocate, new Cloner().deepClone( m_value ) );
    }

    @Nonnull
    @Override
    public final ITerm deepcopy( final IPath... p_prefix )
    {
        return new CRelocateMutexVariable<>(
            ( p_prefix == null ) || ( p_prefix.length == 0 )
            ? m_functor
            : m_functor.append( p_prefix[0] ),
            m_relocate,
            new Cloner().deepClone( m_value )
        );
    }

    @Nonnull
    @Override
    public final IVariable<T> shallowcopysuffix()
    {
        return new CRelocateMutexVariable<>( m_functor, m_relocate );
    }

}
