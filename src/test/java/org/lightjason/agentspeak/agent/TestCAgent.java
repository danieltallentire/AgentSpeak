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

package org.lightjason.agentspeak.agent;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightjason.agentspeak.IBaseTest;
import org.lightjason.agentspeak.action.IAction;
import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;
import org.lightjason.agentspeak.language.variable.CConstant;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * test agent structure
 *
 * @note if a file agentprintin.conf exists on the main directory alls print statements will be shown
 */
@RunWith( DataProviderRunner.class )
public final class TestCAgent extends IBaseTest
{
    /**
     * list with successful plans
     */
    private List<Pair<Boolean, String>> m_testlog;

    static
    {
        // disable logger
        LogManager.getLogManager().reset();
    }

    /**
     * data provider for defining asl files
     * @return triple of test-cases (asl file, number of iterations, expected log items)
     */
    @DataProvider
    public static Object[] generate()
    {
        return Stream.of(
            //new ImmutableTriple<>( "src/test/resources/agent/complete.asl", 5, 0 )
            new ImmutableTriple<>( "src/test/resources/agent/language/crypto.asl", 2, 9 ),
            new ImmutableTriple<>( "src/test/resources/agent/language/math.asl", 2, 10 ),
            new ImmutableTriple<>( "src/test/resources/agent/language/collection.asl", 2, 5 ),
            new ImmutableTriple<>( "src/test/resources/agent/language/generic.asl", 3, 26 ),
            new ImmutableTriple<>( "src/test/resources/agent/language/rules.asl", 2, 4 ),
            new ImmutableTriple<>( "src/test/resources/agent/language/trigger.asl", 3, 21 ),
            new ImmutableTriple<>( "src/test/resources/agent/language/execution.asl", 3, 4 ),
            new ImmutableTriple<>( "src/test/resources/agent/language/webservice.asl", 4, 3 )
        ).toArray();
    }

    /**
     * return list initialize
     */
    @Before
    public final void initialize()
    {
        m_testlog = Collections.synchronizedList( new ArrayList<>() );
    }

    /**
     * test for default generators and configuration
     *
     * @throws Exception on any error
     */
    @Test
    @UseDataProvider( "generate" )
    public final void testASLDefault( final Triple<String, Number, Number> p_asl ) throws Exception
    {
        try
        (
            final InputStream l_stream = new FileInputStream( p_asl.getLeft() )
        )
        {
            final IAgent<?> l_agent = new CAgent.CAgentGenerator(
                                          l_stream,
                                          Stream.concat(
                                              PRINTENABLE
                                              ? Stream.of( new CTestResult() )
                                              : Stream.of( new CTestResult(), new CEmptyPrint() ),
                                              CCommon.actionsFromPackage()
                                          ).collect( Collectors.toSet() )
                                      ).generatesingle();

            IntStream.range( 0, p_asl.getMiddle().intValue() )
                     .forEach( i ->
                     {
                         try
                         {
                             l_agent.call();
                         }
                         catch ( final Exception l_exception )
                         {
                             Assert.assertTrue( MessageFormat.format( "{0}: {1}", p_asl.getLeft(), l_exception.getMessage() ), false );
                         }
                     } );
        }
        catch ( final Exception l_exception )
        {
            Assert.assertTrue( MessageFormat.format( "{0}: {1}", p_asl.getLeft(), l_exception.getMessage() ), false );
        }

        Assert.assertEquals( MessageFormat.format( "{0} {1}", "number of tests", p_asl.getLeft() ), p_asl.getRight().longValue(), m_testlog.size() );
        Assert.assertTrue(
            MessageFormat.format( "{0}", m_testlog.stream().filter( i -> !i.getLeft() ).map( Pair::getRight ).collect( Collectors.toList() ) ),
            m_testlog.stream().anyMatch( Pair::getLeft )
        );
    }

    /**
     * manuell running test
     *
     * @param p_args arguments
     * @throws Exception on parsing exception
     */
    public static void main( final String[] p_args ) throws Exception
    {
        new TestCAgent().invoketest();
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * empty print action
     */
    private static final class CEmptyPrint extends IBaseAction
    {
        /**
         * serial id
         */
        private static final long serialVersionUID = 8344720639088993942L;

        @Nonnull
        @Override
        public final IPath name()
        {
            return CPath.from( "generic/print" );
        }

        @Nonnegative
        @Override
        public final int minimalArgumentNumber()
        {
            return 0;
        }

        @Nonnull
        @Override
        public final IFuzzyValue<Boolean> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                                   @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return
        )
        {
            return CFuzzyValue.from( true );
        }
    }

    /**
     * test action
     */
    private final class CTestResult extends IBaseAction
    {
        /**
         * serial id
         */
        private static final long serialVersionUID = 9032624165822970132L;

        @Nonnull
        @Override
        public final IPath name()
        {
            return CPath.from( "test/result" );
        }

        @Nonnegative
        @Override
        public final int minimalArgumentNumber()
        {
            return 1;
        }

        @Nonnull
        @Override
        public IFuzzyValue<Boolean> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                             @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return
        )
        {
            m_testlog.add(
                new ImmutablePair<>(
                    p_argument.get( 0 ).<Boolean>raw(),
                    p_argument.size() > 1
                    ? p_argument.get( 1 ).<String>raw()
                    : ""
                )
            );

            return CFuzzyValue.from( p_argument.get( 0 ).<Boolean>raw() );
        }
    }

    /**
     * agent class
     */
    private static final class CAgent extends IBaseAgent<IAgent<?>>
    {
        /**
         * serial id
         */
        private static final long serialVersionUID = 7077303993134371057L;

        /**
         * ctor
         *
         * @param p_configuration agent configuration
         */
        private CAgent( final IAgentConfiguration<IAgent<?>> p_configuration )
        {
            super( p_configuration );
        }

        /**
         * agent generator class
         */
        private static final class CAgentGenerator extends IBaseAgentGenerator<IAgent<?>>
        {

            /**
             * ctor
             *
             * @param p_stream input stream
             * @param p_actions set with action
             * @throws Exception thrown on error
             */
            CAgentGenerator( final InputStream p_stream, final Set<IAction> p_actions ) throws Exception
            {
                super( p_stream, p_actions, ( p_agent, p_runningcontext ) -> Stream.of(
                    new CConstant<>( "MyConstInt", 123 ),
                    new CConstant<>( "MyConstString", "here is a test string" )
                ) );
            }

            @Override
            public IAgent<?> generatesingle( final Object... p_data )
            {
                return new CAgent( m_configuration );
            }
        }

    }

}
