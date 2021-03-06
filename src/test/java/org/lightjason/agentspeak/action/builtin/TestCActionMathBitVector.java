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

import cern.colt.bitvector.BitVector;
import cern.colt.matrix.DoubleMatrix1D;
import com.codepoetics.protonpack.StreamUtils;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightjason.agentspeak.IBaseTest;
import org.lightjason.agentspeak.action.IAction;

import org.lightjason.agentspeak.action.builtin.math.bit.vector.CAnd;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CBoolValue;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CCopy;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CCreate;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CFalseCount;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CHammingDistance;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CNAnd;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CNot;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.COr;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CSize;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CTrueCount;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CXor;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CSet;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CClear;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CRange;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CNumericValue;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CToList;
import org.lightjason.agentspeak.action.builtin.math.bit.vector.CToBlas;

import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * test for bit vector actions
 */
@RunWith( DataProviderRunner.class )
public final class TestCActionMathBitVector extends IBaseTest
{
    /**
     * testing vector
     * @note static because of usage in data-provider and test-initialize
     */
    private static final BitVector VECTOR1 = new BitVector( 3 );
    /**
     * testing matrix
     * @note static because of usage in data-provider and test-initialize
     */
    private static final BitVector VECTOR2 = new BitVector( 3 );


    /**
     * initialize
     */
    @Before
    public final void initialize()
    {
        VECTOR1.put( 0, true );
        VECTOR1.put( 1, false );
        VECTOR1.put( 2, false );

        VECTOR2.put( 0, false );
        VECTOR2.put( 1, false );
        VECTOR2.put( 2, true );
    }

    /**
     * data provider generator
     * @return data
     */
    @DataProvider
    public static Object[] generator()
    {
        return testcase(

                Stream.of( VECTOR1, VECTOR2 ),

                Stream.of(
                        CFalseCount.class,
                        CCopy.class,
                        CTrueCount.class,
                        CSize.class,
                        CNot.class,
                        COr.class,
                        CAnd.class,
                        CNAnd.class,
                        CHammingDistance.class,
                        CXor.class
                ),
                Stream.of( 2D, 2D ),
                Stream.of( VECTOR1, VECTOR2 ),
                Stream.of( 1D, 1D ),
                Stream.of( 3, 3 ),
                Stream.of(),
                Stream.of(),
                Stream.of(),
                Stream.of(),
                Stream.of( 2D ),
                Stream.of()

        ).toArray();
    }

    /**
     * method to generate test-cases
     *
     * @param p_input input data
     * @param p_classes matching test-classes / test-cases
     * @param p_classresult result for each class
     * @return test-object
     */
    @SafeVarargs
    @SuppressWarnings( "varargs" )
    private static Stream<Object> testcase( final Stream<Object> p_input, final Stream<Class<?>> p_classes, final Stream<Object>... p_classresult )
    {
        final List<ITerm> l_input = p_input.map( CRawTerm::from ).collect( Collectors.toList() );

        return StreamUtils.zip(
                p_classes,
                Arrays.stream( p_classresult ),
            ( i, j ) -> new ImmutableTriple<>( l_input, i, j )
        );
    }


    /**
     * test all single-input actions
     *
     * @throws IllegalAccessException is thrown on instantiation error
     * @throws InstantiationException is thrown on instantiation error
     */
    @Test
    @UseDataProvider( "generator" )
    public final void action( final Triple<List<ITerm>, Class<? extends IAction>, Stream<Object>> p_input )
            throws IllegalAccessException, InstantiationException
    {
        final List<ITerm> l_return = new ArrayList<>();

        p_input.getMiddle().newInstance().execute(
            false, IContext.EMPTYPLAN,
            p_input.getLeft(),
            l_return
        );

        Assert.assertArrayEquals(
                p_input.getMiddle().toGenericString(),
                l_return.stream().map( ITerm::raw ).toArray(),
                p_input.getRight().toArray()
        );
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
            Stream.of( 3 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertTrue( l_return.get( 0 ).raw() instanceof BitVector );
        Assert.assertEquals( l_return.get( 0 ).<BitVector>raw().size(), 3 );
    }

    /**
     * test boolean value
     */
    @Test
    public final void boolValue()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CBoolValue().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( VECTOR2, 0 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.get( 0 ).<Boolean>raw(), false );
    }

    /**
     * test set
     */
    @Test
    public final void set()
    {
        new CSet().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( VECTOR2, true, 0, 1 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assert.assertEquals( VECTOR2.get( 0 ), true );
        Assert.assertEquals( VECTOR2.get( 1 ), true );
    }

    /**
     * test clear
     */
    @Test
    public final void clear()
    {
        new CClear().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( VECTOR2, 0 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assert.assertEquals( VECTOR2.get( 0 ), false );
    }

    /**
     * test range
     */
    @Test
    public final void range()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CRange().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( VECTOR2, 0, 2 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertEquals( l_return.get( 0 ).<BitVector>raw(), VECTOR2 );
    }

    /**
     * test numericvalue
     */
    @Test
    public final void numericvalue()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CNumericValue().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( VECTOR1, 1 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.get( 0 ).<Number>raw(), 0D );
    }

    /**
     * test toList
     */
    @Test
    public final void tolist()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CToList().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( VECTOR1 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertTrue( l_return.get( 0 ).raw() instanceof List<?> );
        Assert.assertArrayEquals( l_return.get( 0 ).<List<?>>raw().toArray(), Stream.of( 1D, 0D, 0D ).toArray() );
    }

    /**
     * test toblas
     */
    @Test
    public final void toblas()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CToBlas().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( VECTOR2 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertTrue( l_return.get( 0 ).raw() instanceof DoubleMatrix1D );
        Assert.assertArrayEquals( l_return.get( 0 ).<DoubleMatrix1D>raw().toArray(), Stream.of( 0, 0, 1 ).mapToDouble( i -> i ).toArray(), 0 );
    }

    /**
     * test call
     *
     * @param p_args command-line arguments
     */
    public static void main( final String[] p_args )
    {
        new TestCActionMathBitVector().invoketest();
    }

}
