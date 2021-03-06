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

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import com.codepoetics.protonpack.StreamUtils;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightjason.agentspeak.IBaseTest;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.builtin.math.blas.vector.CCopy;
import org.lightjason.agentspeak.action.builtin.math.blas.vector.CAssign;
import org.lightjason.agentspeak.action.builtin.math.blas.vector.CGet;
import org.lightjason.agentspeak.action.builtin.math.blas.vector.CToList;
import org.lightjason.agentspeak.action.builtin.math.blas.vector.CSet;
import org.lightjason.agentspeak.action.builtin.math.blas.vector.CCreate;
import org.lightjason.agentspeak.action.builtin.math.blas.vector.CDotProduct;
import org.lightjason.agentspeak.action.builtin.math.blas.vector.CNonZero;
import org.lightjason.agentspeak.action.builtin.math.blas.vector.CSum;
import org.lightjason.agentspeak.action.builtin.math.blas.vector.CFromList;
import org.lightjason.agentspeak.action.builtin.math.blas.vector.CParse;

import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * test math blas vector functions
 */
@RunWith( DataProviderRunner.class )
public final class TestCActionMathBlasVector extends IBaseTest
{

    /**
     * testing vector
     * @note static because of usage in data-provider
     */
    private static final DoubleMatrix1D VECTOR1 = new DenseDoubleMatrix1D( new double[]{2, 5, 3, 8} );

    /**
     * testing vector
     * @note static because of usage in data-provider
     */
    private static final DoubleMatrix1D VECTOR2 = new DenseDoubleMatrix1D( new double[]{8, 6, 2, 1} );


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
                        CNonZero.class,
                        CSum.class,
                        CDotProduct.class
                ),
                Stream.of( 4D, 4D ),
                Stream.of( VECTOR1.zSum(), VECTOR2.zSum() ),
                Stream.of( 60.0 )

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
     * test all input actions
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
            Stream.of( 2, "dense" ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        new CCreate().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( 4, "sparse" ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 2 );
        Assert.assertEquals( l_return.get( 0 ).<DoubleMatrix1D>raw().size(), 2 );
        Assert.assertEquals( l_return.get( 1 ).<DoubleMatrix1D>raw().size(), 4 );
    }

    /**
     * test set
     */
    @Test
    public final void set()
    {
        final DoubleMatrix1D l_vector = new DenseDoubleMatrix1D( 4 );

        new CSet().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( 0, 6.0, l_vector ).map( CRawTerm::from ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assert.assertEquals( l_vector.get( 0 ), 6, 0 );
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
        Assert.assertTrue( l_return.get( 0 ).raw() instanceof List );

        final List<Number> l_tolist = l_return.get( 0 ).raw();
        Assert.assertArrayEquals( l_tolist.toArray(), Stream.of( 2.0, 5.0, 3.0, 8.0 ).collect( Collectors.toList() ).toArray() );
    }

    /**
     * test assign scalar
     */
    @Test
    public final void assignscalar()
    {
        final DoubleMatrix1D l_vector = new DenseDoubleMatrix1D( 4 );

        new CAssign().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( 2, l_vector ).map( CRawTerm::from ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assert.assertArrayEquals( l_vector.toArray(), Stream.of( 2, 2, 2, 2 ).mapToDouble( i -> i ).toArray(), 0 );
    }

    /**
     * test assign vector
     */
    @Test
    public final void assignvector()
    {
        final DoubleMatrix1D l_vector = new DenseDoubleMatrix1D( 4 );

        new CAssign().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( VECTOR2, l_vector ).map( CRawTerm::from ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assert.assertArrayEquals( l_vector.toArray(), VECTOR2.toArray(), 0 );
    }


    /**
     * test get
     */
    @Test
    public final void get()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CGet().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( VECTOR1, 0 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertTrue( l_return.get( 0 ).raw() instanceof Double );
        Assert.assertEquals( l_return.get( 0 ).<Double>raw(), 2, 0 );
    }

    /**
     * test copy
     */
    @Test
    public final void copy()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CCopy().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( VECTOR1, VECTOR2 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 2 );
        Assert.assertArrayEquals( l_return.stream().map( ITerm::raw ).toArray(), Stream.of( VECTOR1, VECTOR2 ).toArray() );
    }

    /**
     * test parse
     */
    @Test
    public final void parse()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CParse().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( "1,2,3", "dense" ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        new CParse().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( "4,3,4", "sparse" ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 2 );
        Assert.assertArrayEquals( l_return.get( 0 ).<DoubleMatrix1D>raw().toArray(), new double[]{1, 2, 3}, 0 );
        Assert.assertArrayEquals( l_return.get( 1 ).<DoubleMatrix1D>raw().toArray(), new double[]{4, 3, 4}, 0 );
    }

    /**
     * test fromlist
     */
    @Test
    public final void fromlist()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CFromList().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( Stream.of( 1, 2, 3 ).collect( Collectors.toList() ), "dense" ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        new CFromList().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( Stream.of( 4, 3, 4 ).collect( Collectors.toList() ), "sparse" ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 2 );
        Assert.assertArrayEquals( l_return.get( 0 ).<DoubleMatrix1D>raw().toArray(), new double[]{1, 2, 3}, 0 );
        Assert.assertArrayEquals( l_return.get( 1 ).<DoubleMatrix1D>raw().toArray(), new double[]{4, 3, 4}, 0 );
    }


    /**
     * test call
     *
     * @param p_args command-line arguments
     */
    public static void main( final String[] p_args )
    {
        new TestCActionMathBlasVector().invoketest();
    }
}
