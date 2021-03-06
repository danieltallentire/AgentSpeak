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

import org.junit.Assert;
import org.junit.Test;
import org.lightjason.agentspeak.IBaseTest;
import org.lightjason.agentspeak.action.builtin.generic.type.CCreateLiteral;
import org.lightjason.agentspeak.action.builtin.generic.type.CIs;
import org.lightjason.agentspeak.action.builtin.generic.type.CIsNull;
import org.lightjason.agentspeak.action.builtin.generic.type.CIsNumeric;
import org.lightjason.agentspeak.action.builtin.generic.type.CIsString;
import org.lightjason.agentspeak.action.builtin.generic.type.CParseNumber;
import org.lightjason.agentspeak.action.builtin.generic.type.CParseLiteral;
import org.lightjason.agentspeak.action.builtin.generic.type.CToNumber;
import org.lightjason.agentspeak.action.builtin.generic.type.CToString;
import org.lightjason.agentspeak.action.builtin.generic.type.CType;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * test generic-type actions
 */
public final class TestCActionGenericType extends IBaseTest
{

    /**
     * test create literal action
     */
    @Test
    public final void createliteral()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CCreateLiteral().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( "functor", "stringvalue", 1234, true ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertEquals(
            l_return.get( 0 ).<ILiteral>raw(),
            CLiteral.from(
                    "functor",
                    CRawTerm.from( "stringvalue" ),
                    CRawTerm.from( 1234 ),
                    CRawTerm.from( true )
            )
        );
    }


    /**
     * test parse literal action
     */
    @Test
    public final void parseliteral()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CParseLiteral().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( "main/parsefunctor( \"hello\", 666, false )" ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 1 );
        Assert.assertEquals(
            l_return.get( 0 ).<ILiteral>raw(),
            CLiteral.from(
                    "main/parsefunctor",
                    CRawTerm.from( "hello" ),
                    CRawTerm.from( 666D ),
                    CRawTerm.from( false )
            )
        );
    }


    /**
     * test parse literal action with error
     */
    @Test
    public final void parseliteralerror()
    {
        final List<ITerm> l_return = new ArrayList<>();

        Assert.assertFalse(
            new CParseLiteral().execute(
                false, IContext.EMPTYPLAN,
                Stream.of( "Main/parsefunctor( hello, XXXXX, false )" ).map( CRawTerm::from ).collect( Collectors.toList() ),
                l_return
            ).value()
        );
    }


    /**
     * test parse-float action
     */
    @Test
    public final void parsefloat()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CParseNumber().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( "732.489", "64.091248", "-78129.01", "foo" ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 4 );
        Assert.assertEquals( l_return.get( 0 ).<Number>raw().doubleValue(), 732.489, 0 );
        Assert.assertEquals( l_return.get( 1 ).<Number>raw().doubleValue(), 64.091248, 0 );
        Assert.assertEquals( l_return.get( 2 ).<Number>raw().doubleValue(), -78129.01, 0 );
        Assert.assertNull( l_return.get( 3 ).raw() );
    }


    /**
     * test type action
     */
    @Test
    public final void type()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CType().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( new ArrayList<>(), 123L, "test value", new HashSet<>() ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 4 );
        Assert.assertEquals( l_return.get( 0 ).<String>raw(), "java.util.ArrayList" );
        Assert.assertEquals( l_return.get( 1 ).<String>raw(), "java.lang.Long" );
        Assert.assertEquals( l_return.get( 2 ).<String>raw(), "java.lang.String" );
        Assert.assertEquals( l_return.get( 3 ).<String>raw(), "java.util.HashSet" );
    }


    /**
     * test "is" action
     */
    @Test
    public final void is()
    {
        Assert.assertFalse(
            new CIs().execute(
                false, IContext.EMPTYPLAN,
                Stream.of( "java.lang.String", "text foo", 123, 88.98 ).map( CRawTerm::from ).collect( Collectors.toList() ),
                Collections.emptyList()
            ).value()
        );

        Assert.assertTrue(
            new CIs().execute(
                false, IContext.EMPTYPLAN,
                Stream.of( "java.lang.Number", 123, 44.5 ).map( CRawTerm::from ).collect( Collectors.toList() ),
                Collections.emptyList()
            ).value()
        );
    }


    /**
     * test "isnull"action
     */
    @Test
    public final void isnull()
    {
        Assert.assertFalse(
            new CIsNull().execute(
                false, IContext.EMPTYPLAN,
                Stream.of( "test type string", null ).map( CRawTerm::from ).collect( Collectors.toList() ),
                Collections.emptyList()
            ).value()
        );

        Assert.assertTrue(
            new CIsNull().execute(
                false, IContext.EMPTYPLAN,
                Stream.of( CRawTerm.from( null ) ).collect( Collectors.toList() ),
                Collections.emptyList()
            ).value()
        );
    }


    /**
     * test "isnumeric" action
     */
    @Test
    public final void isnumeric()
    {
        Assert.assertFalse(
            new CIsNumeric().execute(
                false, IContext.EMPTYPLAN,
                Stream.of( "test type string", 123, 77L, 112.123, 44.5f ).map( CRawTerm::from ).collect( Collectors.toList() ),
                Collections.emptyList()
            ).value()
        );

        Assert.assertTrue(
            new CIsNumeric().execute(
                false, IContext.EMPTYPLAN,
                Stream.of( 123, 77L, 112.123, 44.5f ).map( CRawTerm::from ).collect( Collectors.toList() ),
                Collections.emptyList()
            ).value()
        );
    }


    /**
     * test "isstring" action
     */
    @Test
    public final void isstring()
    {
        Assert.assertFalse(
            new CIsString().execute(
                false, IContext.EMPTYPLAN,
                Stream.of( "test foobar", 123, "string again", true, new Object(), 77.8, 'a' ).map( CRawTerm::from ).collect( Collectors.toList() ),
                Collections.emptyList()
            ).value()
        );


        Assert.assertTrue(
            new CIsString().execute(
                false, IContext.EMPTYPLAN,
                Stream.of( "okay 1", 'c', "ok 2" ).map( CRawTerm::from ).collect( Collectors.toList() ),
                Collections.emptyList()
            ).value()
        );
    }


    /**
     * test "tostring"
     */
    @Test
    public final void tostring()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CToString().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( "", 123, 5.5, new Object() ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 4 );
        Assert.assertTrue( l_return.get( 0 ).<String>raw().isEmpty() );
        Assert.assertEquals( l_return.get( 1 ).raw(), "123" );
        Assert.assertEquals( l_return.get( 2 ).raw(), "5.5" );
        Assert.assertTrue( l_return.get( 3 ).raw() instanceof String );
    }


    /**
     * test "tofloat"
     */
    @Test
    public final void tofloat()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CToNumber().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( 1, 2, 3.2 ).map( CRawTerm::from ).collect( Collectors.toList() ),
            l_return
        );

        Assert.assertEquals( l_return.size(), 3 );
        Assert.assertTrue( l_return.get( 0 ).raw() instanceof Double );
        Assert.assertTrue( l_return.get( 1 ).raw() instanceof Double );
        Assert.assertTrue( l_return.get( 2 ).raw() instanceof Double );
    }


    /**
     * test "tofloat" error
     */
    @Test
    public final void tofloaterror()
    {
        Assert.assertFalse(
            new CToNumber().execute(
                false, IContext.EMPTYPLAN,
                Stream.of( "" ).map( CRawTerm::from ).collect( Collectors.toList() ),
                Collections.emptyList()
            ).value()
        );
    }


    /**
     * test call
     *
     * @param p_args command-line arguments
     */
    public static void main( final String[] p_args )
    {
        new TestCActionGenericType().invoketest();
    }

}
