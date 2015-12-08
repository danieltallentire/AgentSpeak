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

package lightjason.language.plan.action;

import lightjason.beliefbase.IBeliefBaseMask;
import lightjason.common.CCommon;
import lightjason.language.ILiteral;
import lightjason.language.plan.IPlan;
import lightjason.language.plan.fuzzy.CBoolean;

import java.text.MessageFormat;
import java.util.Map;


/**
 * belief action
 */
public final class CBeliefAction extends IAction<ILiteral>
{
    /**
     * running action
     */
    private final EAction m_action;

    /**
     * ctor
     *
     * @param p_literal literal
     * @param p_action action
     */
    public CBeliefAction( final ILiteral p_literal, final EAction p_action )
    {
        super( p_literal );
        m_action = p_action;
    }

    @Override
    public String toString()
    {
        return MessageFormat.format( "{0}{1}", m_action, m_value );
    }

    /**
     * @todo change literal event is missing
     * @todo disable actions
     */
    @Override
    public CBoolean execute( final IBeliefBaseMask p_beliefbase, final Map<ILiteral, IPlan> p_runningplan )
    {
        switch ( m_action )
        {
            case Add:
                //p_beliefbase.add( m_data.getFunctorPath(), m_data );
                break;

            case Delete:
                //p_beliefbase.remove( m_data.getFunctorPath(), m_data );
                break;

            default:
                throw new IllegalArgumentException( CCommon.getLanguageString( this, "unknownaction", m_action ) );
        }

        return new CBoolean( true );
    }

    /**
     * belief action definition
     */
    public enum EAction
    {
        Add( "+" ),
        Delete( "-" ),
        Change( "-+" );

        /**
         * name
         */
        private final String m_name;

        /**
         * ctor
         *
         * @param p_name string represenation
         */
        private EAction( final String p_name )
        {
            m_name = p_name;
        }

        @Override
        public String toString()
        {
            return m_name;
        }
    }
}