/**
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the Light-Jason                                               #
 * # Copyright (c) 2015-16, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
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

package lightjason.agentspeak.language.execution.action.achievement_test;

import lightjason.agentspeak.language.ITerm;
import lightjason.agentspeak.language.execution.IContext;
import lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;
import lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import lightjason.agentspeak.language.variable.IVariable;
import lightjason.agentspeak.language.variable.IVariableEvaluate;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Stream;


/**
 * achievement-goal action based on variables
 */
public final class CAchievementGoalVariable extends IAchievementGoal<IVariableEvaluate>
{

    /**
     * ctor
     *
     * @param p_type value of the achievment-goal
     * @param p_immediately immediately execution
     */
    public CAchievementGoalVariable( final IVariableEvaluate p_type, final boolean p_immediately )
    {
        super( p_type, p_immediately );
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "{0}{1}", m_immediately ? "!!" : "!", m_value );
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel, final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation
    )
    {
        return p_context.getAgent().trigger(
            CTrigger.from(
                ITrigger.EType.ADDGOAL,
                m_value.evaluate( p_context )
            ),
            m_immediately
        );
    }

    @Override
    public final Stream<IVariable<?>> getVariables()
    {
        return m_value.getVariables();
    }
}