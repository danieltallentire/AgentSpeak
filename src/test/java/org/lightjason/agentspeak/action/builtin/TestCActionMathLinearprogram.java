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

package org.lightjason.agentspeak.action.builtin;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.Relationship;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lightjason.agentspeak.IBaseTest;
import org.lightjason.agentspeak.action.builtin.math.linearprogram.CCreate;
import org.lightjason.agentspeak.action.builtin.math.linearprogram.CSolve;
import org.lightjason.agentspeak.action.builtin.math.linearprogram.CValueConstraint;
import org.lightjason.agentspeak.action.builtin.math.linearprogram.CEquationConstraint;

import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * test math linear program functions
 */
public final class TestCActionMathLinearprogram extends IBaseTest
{
    /**
     * testing linear program
     */
    private ImmutablePair<LinearObjectiveFunction, Collection<LinearConstraint>> m_linearprogram;


    /**
     * initialize
     */
    @Before
    public final void initialize()
    {
        m_linearprogram = new ImmutablePair<>( new LinearObjectiveFunction( new double[]{}, 0.0 ), new HashSet<LinearConstraint>() );
    }



    /**
     * test create
     */
    @Test
    public final void create()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CCreate().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( 2, 3, 4, 2 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertNotNull( l_return.get( 0 ).<ImmutablePair<LinearObjectiveFunction, Collection<LinearConstraint>>>raw().getLeft() );
        Assert.assertTrue( l_return.get( 0 ).<ImmutablePair<LinearObjectiveFunction, Collection<LinearConstraint>>>raw().getRight().isEmpty() );
    }

    /**
     * test value constraint
     */
    @Test
    public final void valueconstraint()
    {
        new CValueConstraint().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( m_linearprogram, 2.0, 2.0, 12.0, 19.0, "=", 11.0 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assert.assertEquals( m_linearprogram.getRight().size(), 1 );
        Assert.assertEquals( 11.0,  m_linearprogram.getRight().iterator().next().getValue(), 0 );
    }


    /**
     * test equation constraint
     */
    @Test
    public final void equationconstraint()
    {
        final LinearConstraint l_result = new LinearConstraint( new double[]{2, 7, 12}, 19.0, Relationship.EQ, new double[]{1, 2, 3}, 5.0 );

        new CEquationConstraint().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( m_linearprogram, 2, 7, 12, 19.0, "=", 1, 2, 3, 5.0 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            Collections.emptyList()
        );


        Assert.assertEquals( m_linearprogram.getRight().size(), 1 );
        Assert.assertArrayEquals(
            Stream.of(
                l_result.getValue(),
                l_result.getRelationship(),
                l_result.getCoefficients()
            ).toArray(),

            Stream.of(
                m_linearprogram.getRight().iterator().next().getValue(),
                m_linearprogram.getRight().iterator().next().getRelationship(),
                m_linearprogram.getRight().iterator().next().getCoefficients()
            ).toArray()
        );
    }

    /**
     * test solve maximum
     */
    @Test
    public final void solvemaximize()
    {
        final List<ITerm> l_return = new ArrayList<>();
        final ImmutablePair<LinearObjectiveFunction, Collection<LinearConstraint>> l_linearprogrammax = new ImmutablePair<>(
                new LinearObjectiveFunction( new double[]{3, 5}, 0.0 ),
                new HashSet<LinearConstraint>()
        );

        l_linearprogrammax.getRight().add( new LinearConstraint( new double[] {2, 8}, Relationship.LEQ, 13 ) );
        l_linearprogrammax.getRight().add( new LinearConstraint( new double[] {5, -1}, Relationship.LEQ, 11 ) );
        l_linearprogrammax.getRight().add( new LinearConstraint( new double[] {1, 0}, Relationship.GEQ, 0 ) );
        l_linearprogrammax.getRight().add( new LinearConstraint( new double[] {0, 1}, Relationship.GEQ, 0 ) );


        new CSolve().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( l_linearprogrammax, "maximize", "non-negative" ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertArrayEquals(
            l_return.stream().map( ITerm::raw ).toArray(),
            Stream.of( 12.333333333333332, 2, 2.4047619047619047, 1.0238095238095237 ).toArray()
        );
    }

    /**
     * test solve minimize
     */
    @Test
    public final void solveminimize()
    {
        final List<ITerm> l_return = new ArrayList<>();
        final ImmutablePair<LinearObjectiveFunction, Collection<LinearConstraint>> l_linearprogrammin = new ImmutablePair<>(
            new LinearObjectiveFunction( new double[]{-2, 15}, 0.0 ),
            new HashSet<LinearConstraint>()
        );

        l_linearprogrammin.getRight().add( new LinearConstraint( new double[] {-6, 8}, Relationship.GEQ, 3 ) );
        l_linearprogrammin.getRight().add( new LinearConstraint( new double[] {5, -1}, Relationship.GEQ, 11 ) );
        l_linearprogrammin.getRight().add( new LinearConstraint( new double[] {1, 0}, Relationship.GEQ, 0 ) );
        l_linearprogrammin.getRight().add( new LinearConstraint( new double[] {0, 1}, Relationship.GEQ, 0 ) );

        new CSolve().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( l_linearprogrammin, "minimize", "non-negative" ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertArrayEquals(
            l_return.stream().map( ITerm::raw ).toArray(),
            Stream.of( 30.38235294117647, 2, 2.676470588235294, 2.3823529411764706 ).toArray()
        );
    }

    /**
     * test call
     *
     * @param p_args command-line arguments
     */
    public static void main( final String[] p_args )
    {
        new TestCActionMathLinearprogram().invoketest();
    }
}
