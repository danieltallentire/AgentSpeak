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

/**
 * base grammar rules of an additional version of AgentSpeak(L) without any terminal symbols,
 * the rules are restricted to the AgentSpeak elements e.g. beliefs, plan, ...
 **/
grammar AgentSpeak;
import Terminal;


// --- agent-behaviour structure ---------------------------------------------------------

/**
 * belief rule
 **/
belief :
    STRONGNEGATION? literal DOT
    ;

/**
 * agent plans rule
 * @note one plan must exists
 **/
plans :
    plan+
    ;

/**
 * optional (prolog) rules
 **/
principles :
    principle+
    ;

/**
 * plan modified against the original Jason grammar,
 * so a context is optional (on default true) and the
 * plan body is also optional. The definition is
 * trigger name [ plancontent ]* .
 */
plan :
    annotations?
    plan_trigger
    literal
    plandefinition*
    DOT
    ;

/**
 * plan body & context definition
 * The definition is [ : condition ] [ <- body ]
 **/
plandefinition :
    ( COLON plan_context )?
    ARROW body
    ;

/**
 * rules are similar to plans
 * but without context and trigger event
 **/
principle :
    annotations?
    literal
    RULEOPERATOR
    body
    DOT
    ;

/**
 * annotation for rules and plans
 **/
annotations :
    ( annotation_atom | annotation_literal )+
    ;

/**
 * atomic annotations (without parameter)
 **/
annotation_atom :
    AT
    (ATOMIC | EXCLUSIVE | PARALLEL)
    ;

/**
 * annotation with parameter
 **/
annotation_literal :
    AT
    ( annotation_numeric_literal | annotation_symbolic_literal )
    ;

/**
 * annotations with numerical parameter
 **/
annotation_numeric_literal :
    ( FUZZY | SCORE )
    LROUNDBRACKET
    number
    RROUNDBRACKET
    ;

/**
 * annotation with symbolic value
 **/
annotation_symbolic_literal :
    EXPIRES
    LROUNDBRACKET
    atom
    RROUNDBRACKET
    ;

/**
 * plan trigger which can match a goal or belief
 **/
plan_trigger :
    (plan_belief_trigger | plan_goal_trigger)
    ;

/**
 * plan trigger for a goal
 **/
plan_goal_trigger :
    (PLUS | MINUS)
    EXCLAMATIONMARK
    ;

/**
 * plan trigger for a belief
 **/
plan_belief_trigger :
    PLUS | MINUS
    ;

/**
 * logical context for plan matching
 **/
plan_context :
    expression
    ;

/**
 * plan or block body
 **/
body :
    body_formula
    ( SEMICOLON body_formula )*
    ;
// ---------------------------------------------------------------------------------------



// --- agent-expression-context ----------------------------------------------------------
body_formula :
    belief_action
    | achievement_goal_action
    | test_goal_action
    | unary_expression
    | assignment_expression
    | deconstruct_expression
    //| foreach_loop
    //| while_loop
    //| for_loop
    //| if_else
    | term
    ;

/**
 * belief-action operator
 **/
belief_action :
    ( PLUS | MINUS | MINUSPLUS ) literal
    ;

/**
 * achivement-goal action
 **/
achievement_goal_action :
    ( EXCLAMATIONMARK | DOUBLEEXCLAMATIONMARK ) literal
    ;

/**
 * test-goal action
 **/
test_goal_action :
    QUESTIONMARK literal
    ;

/**
 * unary expression
 **/
unary_expression :
    variable
    unaryoperator
    ;

/**
 * deconstruct expression (splitting clauses)
 **/
deconstruct_expression :
    variablelist
    DECONSTRUCT
    ( literal | variable )
    ;

/**
 * assignment expression (for assignin a variable)
 **/
assignment_expression :
    variable
    ASSIGN
    ( term | expression )
    ;

/**
 * numerical / logical expression
 **/
expression :
    LROUNDBRACKET expression_logic_or RROUNDBRACKET
    ;

/**
 * logical negotiation
 **/
expression_logic_negation :
    NEGATION? expression
    ;

/**
 * logical or expression
 **/
expression_logic_or :
    expression_logic_and ( OR expression_logic_and )*
    ;

/**
 * logical and expression
 **/
expression_logic_and :
    expression_equal ( AND expression_equal )*
    ;

/**
 * equal expression
 **/
expression_equal :
    expression_relation ( (EQUAL | NOTEQUAL) expression_relation )*
    ;

/**
 * relation expression
 **/
expression_relation :
    expression_numeric_additive ( (LESS | LESSEQUAL | GREATER | GREATEREQUAL) expression_numeric_additive )*
    ;

/**
 * numeric addition expression
 **/
expression_numeric_additive :
    expression_numeric_multiply ( (PLUS | MINUS) expression_numeric_multiply )*
    ;

/**
 * numeric multiply expression
 **/
expression_numeric_multiply :
    numeric_element ( (MULTIPLY | DIVIDE | MODULO ) numeric_element )*
    ;

logical_element :
    logicalvalue
    | variable
    | literal
    ;

numeric_element :
    number
    | variable
    | literal
    ;

block_formula :
    LCURVEDBRACKET body RCURVEDBRACKET
    | body_formula
    ;

if_else :
    IF LROUNDBRACKET expression RROUNDBRACKET
    block_formula
    ( ELSE block_formula )?
    ;

while_loop :
    WHILE LROUNDBRACKET expression RROUNDBRACKET
    block_formula
    ;

for_loop :
    FOR LROUNDBRACKET assignment_expression? SEMICOLON expression SEMICOLON assignment_expression? RROUNDBRACKET
    block_formula
    ;

foreach_loop :
    FOR LROUNDBRACKET term RROUNDBRACKET
    block_formula
    ;
// ---------------------------------------------------------------------------------------



// --- complex-data-types ----------------------------------------------------------------

/**
 * clause represent a literal structure existing
 * atom, optional argument, optional annotations
 **/
literal :
    AT?
    atom
    ( LROUNDBRACKET termlist? RROUNDBRACKET )?
    ( LANGULARBRACKET literalset? RANGULARBRACKET )?
    ;

/**
 * terms are predictable structures
 **/
term :
    string
    | number
    | logicalvalue
    | literal
    | expression
    | variable
    | variablelist
    | LANGULARBRACKET termlist RANGULARBRACKET
    ;

/**
 * generic list equal to collcations with empty clause
 **/
termlist :
    term ( COMMA term )*
    ;

/**
 * specified list only with literals and empty clause
 **/
literalset :
    literal ( COMMA literal )*
    ;

/**
 * list with head-tail-annotation definition
 **/
variablelist :
    LANGULARBRACKET
    variable (LISTSEPARATOR variable)+
    RANGULARBRACKET
    ;

/**
 * atoms are defined like Prolog atoms
 * @note internal action in Jason can begin with a dot, but here it is removed
 **/
atom :
    LOWERCASELETTER
    ( LOWERCASELETTER | UPPERCASELETTER | UNDERSCORE | DIGIT | SLASH )*
    ;

/**
 * variables are defined like Prolog variables,
 * @-prefix creates a thread-safe variable
 **/
variable :
    AT?
    ( UPPERCASELETTER | UNDERSCORE )
    ( LOWERCASELETTER | UPPERCASELETTER | UNDERSCORE | DIGIT | SLASH )*
    ;

/**
 * unary operator
 **/
unaryoperator :
    INCREMENT
    | DECREMENT
    ;

/**
 * binary operator
 **/
binaryoperator :
    ASSIGNINCREMENT
    | ASSIGNDECREMENT
    | ASSIGNMULTIPLY
    | ASSIGNDIVIDE
    ;

/**
 * default behaviour in Jason is only a floating-point number (double)
 * but here exists the difference between floating and integral number
 * types within the grammar, the div-operator (integer division) is removed,
 * also definied constants are used
 **/
number :
    floatnumber
    | integernumber
    ;

/**
 * floating-point number
 **/
floatnumber :
    MINUS?
    ( DIGIT+ DOT DIGIT+ | constant )
    ;

/**
 * integer number
 **/
integernumber :
    MINUS? DIGIT+
    ;

/**
 * boolean values
 **/
logicalvalue :
    TRUE
    | FALSE
    ;

/**
 * floating-point constants
 **/
constant :
    PI
    | EULER
    | GRAVITY
    | AVOGADRO
    | BOLTZMANN
    | ELECTRON
    | PROTON
    | NEUTRON
    | LIGHTSPEED
    | INFINITY
    ;

/**
 * string define with single or double quotes
 **/
string :
    SINGLEQUOTESTRING
    | DOUBLEQUOTESTRING
    ;
// ---------------------------------------------------------------------------------------




