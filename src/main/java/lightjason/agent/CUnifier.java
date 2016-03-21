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

package lightjason.agent;

import com.codepoetics.protonpack.StreamUtils;
import lightjason.language.CCommon;
import lightjason.language.ILiteral;
import lightjason.language.ITerm;
import lightjason.language.IVariable;
import lightjason.language.execution.IContext;
import lightjason.language.execution.IUnifier;
import lightjason.language.execution.expression.IExpression;
import lightjason.language.execution.fuzzy.CBoolean;
import lightjason.language.execution.fuzzy.IFuzzyValue;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * unification algorithm
 *
 * @todo incomplete
 */
public final class CUnifier implements IUnifier
{

    // --- inheritance & context modification ------------------------------------------------------------------------------------------------------------------

    @Override
    public final IFuzzyValue<Boolean> parallelunify( final IContext<?> p_context, final ILiteral p_literal, final long p_variablenumber,
                                                     final IExpression p_expression
    )
    {
        // get all possible variables
        final List<Set<IVariable<?>>> l_variables = unify( p_context.getAgent(), p_literal, p_variablenumber );
        if ( l_variables.isEmpty() )
            return CBoolean.from( false );

        // if no expression exists, returns the first unified structure
        if ( p_expression == null )
        {
            updatecontext( p_context, l_variables.get( 0 ).parallelStream() );
            return CBoolean.from( true );
        }

        // otherwise the expression must be checked, first match will be used
        final Set<IVariable<?>> l_result = l_variables.parallelStream()
                                                      .filter( i -> {
                                                          final List<ITerm> l_return = new LinkedList<>();
                                                          p_expression.execute(
                                                                  updatecontext(
                                                                          p_context.duplicate(),
                                                                          i.parallelStream()
                                                                  ),
                                                                  false,
                                                                  Collections.<ITerm>emptyList(),
                                                                  l_return,
                                                                  Collections.<ITerm>emptyList()
                                                          );
                                                          return ( l_return.size() == 1 ) && ( CCommon.<Boolean, ITerm>getRawValue( l_return.get( 0 ) ) );
                                                      } )
                                                      .findFirst()
                                                      .orElse( Collections.<IVariable<?>>emptySet() );

        // if no match
        if ( l_result.isEmpty() )
            return CBoolean.from( false );

        updatecontext( p_context, l_result.parallelStream() );
        return CBoolean.from( true );
    }

    @Override
    public final IFuzzyValue<Boolean> sequentialunify( final IContext<?> p_context, final ILiteral p_literal, final long p_variablenumber,
                                                       final IExpression p_expression
    )
    {
        // get all possible variables
        final List<Set<IVariable<?>>> l_variables = unify( p_context.getAgent(), p_literal, p_variablenumber );
        if ( l_variables.isEmpty() )
            return CBoolean.from( false );

        // if no expression exists, returns the first unified structure
        if ( p_expression == null )
        {
            updatecontext( p_context, l_variables.get( 0 ).parallelStream() );
            return CBoolean.from( true );
        }

        // otherwise the expression must be checked, first match will be used
        final Set<IVariable<?>> l_result = l_variables.stream()
                                                      .filter( i -> {
                                                          final List<ITerm> l_return = new LinkedList<>();
                                                          p_expression.execute(
                                                                  updatecontext(
                                                                          p_context.duplicate(),
                                                                          i.parallelStream()
                                                                  ),
                                                                  false,
                                                                  Collections.<ITerm>emptyList(),
                                                                  l_return,
                                                                  Collections.<ITerm>emptyList()
                                                          );
                                                          return ( l_return.size() == 1 ) && ( CCommon.<Boolean, ITerm>getRawValue( l_return.get( 0 ) ) );
                                                      } )
                                                      .findFirst()
                                                      .orElse( Collections.<IVariable<?>>emptySet() );

        // if no match
        if ( l_result.isEmpty() )
            return CBoolean.from( false );

        updatecontext( p_context, l_result.parallelStream() );
        return CBoolean.from( true );
    }

    /**
     * updates within an instance context all variables with the unified values
     *
     * @param p_context context
     * @param p_unifiedvariables unified variables as stream
     * @return context reference
     */
    private static IContext<?> updatecontext( final IContext<?> p_context, final Stream<IVariable<?>> p_unifiedvariables )
    {
        p_unifiedvariables.forEach( i -> p_context.getInstanceVariables().get( i.getFQNFunctor() ).set( i.getTyped() ) );
        return p_context;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- unifying algorithm of a literal ---------------------------------------------------------------------------------------------------------------------

    /**
     * search all relevant literals within the agent beliefbase and unifies the variables
     *
     * @param p_agent agent
     * @param p_literal literal search
     * @param p_variablenumber number of unified variables
     * @return list of literal sets
     **/
    private static List<Set<IVariable<?>>> unify( final IAgent p_agent, final ILiteral p_literal, final long p_variablenumber )
    {
        final List<Set<IVariable<?>>> l_variables = unifyexact( p_agent, p_literal, p_variablenumber );
        if ( l_variables.isEmpty() )
            l_variables.addAll( unifyrecursive( p_agent, p_literal, p_variablenumber ) );

        return l_variables;
    }

    /**
     * search all relevant literals within the agent beliefbase and unifies the variables
     *
     * @param p_agent agent
     * @param p_literal literal search
     * @param p_variablenumber number of unified variables
     * @return list of literal sets
     **/
    @SuppressWarnings( "unchecked" )
    private static List<Set<IVariable<?>>> unifyexact( final IAgent p_agent, final ILiteral p_literal, final long p_variablenumber )
    {
        return p_agent.getBeliefBase()
                      .parallelStream( p_literal.isNegated(), p_literal.getFQNFunctor() )
                      .filter( i -> ( i.valuehash() == p_literal.valuehash() ) &&
                                    ( i.annotationhash() == p_literal.annotationhash() )
                      )
                      .map( i -> {
                          final ILiteral l_literal = (ILiteral) p_literal.deepcopy();
                          return Stream.concat(
                                  unifyexact( CCommon.recursiveterm( l_literal.orderedvalues() ), CCommon.recursiveterm( i.orderedvalues() ) ),
                                  unifyexact( CCommon.recursiveliteral( l_literal.annotations() ), CCommon.recursiveliteral( i.annotations() ) )
                          ).collect( Collectors.toSet() );
                      } )
                      .filter( i -> p_variablenumber == i.size() )
                      .collect( Collectors.toList() );
    }

    /**
     * search all relevant literals within the agent beliefbase and unifies the variables
     *
     * @param p_agent agent
     * @param p_literal literal search
     * @param p_variablenumber number of unified variables
     * @return list of literal sets
     *
     * @todo search variables recursive and store path of the variable, get based on the path the values,
     * use also hash-codes to get value checks
     **/
    @SuppressWarnings( "unchecked" )
    private static List<Set<IVariable<?>>> unifyrecursive( final IAgent p_agent, final ILiteral p_literal, final long p_variablenumber )
    {
        return p_agent.getBeliefBase()
                      .parallelStream( p_literal.isNegated(), p_literal.getFQNFunctor() )
                      .map( i -> unifyrecursive( (ILiteral) p_literal.deepcopy(), p_literal ) )
                      .filter( i -> p_variablenumber == i.size() )
                      .collect( Collectors.toList() );
    }


    /**
     * runs the exact (hash equal) unifiying process
     * @note stucture of input literals are equal, so only a element-wise check is
     * needed. If an element in the target literal stream is a variable unification success
     * and variable will be unified, otherwise elements in the source and target literal must
     * be equal
     *
     * @param p_target term stream of targets (literal which stores the variables as instance)
     * @param p_source term stream of sources
     * @return stream with unified variables
     */
    @SuppressWarnings( "unchecked" )
    private static Stream<IVariable<?>> unifyexact( final Stream<ITerm> p_target, final Stream<ITerm> p_source )
    {
        final Set<IVariable<?>> l_result = new HashSet<>();
        if ( !StreamUtils.zip(
                p_source,
                p_target,
                ( s, t ) -> {
                    if ( t instanceof IVariable<?> )
                    {
                        l_result.add( ( (IVariable<Object>) t ).set( s ) );
                        return true;
                    }
                    return s.equals( t );
                }
        ).allMatch( i -> i ) )
            return Collections.<IVariable<?>>emptySet().stream();

        return l_result.stream();
    }

    /**
     * runs the fuzzy (hash unequal) unifiying process
     *
     * @param p_target literal of targets (literal which stores the variables as instance)
     * @param p_source literal sources
     * @return set with unified variables
     * @bug incomplete
     */
    private static Set<IVariable<?>> unifyrecursive( final ILiteral p_target, final ILiteral p_source )
    {
        // target && source elements must be equal, if not equal ignore literal
        // variables can be match with literals or values
        // iterate over each literal, if element is a variable -> unify, otherwise literals must be equal or unifyable
        final Set<IVariable<?>> l_result = new HashSet<>();

        return l_result;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

}
