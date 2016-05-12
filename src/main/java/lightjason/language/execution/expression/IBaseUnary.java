package lightjason.language.execution.expression;

import lightjason.agent.IAgent;
import lightjason.common.CCommon;
import lightjason.error.CIllegalArgumentException;
import lightjason.language.variable.IVariable;

import java.text.MessageFormat;
import java.util.stream.Stream;


/**
 * base unary expression
 */
public abstract class IBaseUnary implements IUnaryExpression
{
    /**
     * expression operator
     */
    protected final EOperator m_operator;
    /**
     * left-hand-side argument
     */
    protected final IExpression m_expression;


    /**
     * ctor
     *
     * @param p_operator operator
     * @param p_expression expression
     */
    protected IBaseUnary( final EOperator p_operator, final IExpression p_expression )
    {
        if ( !p_operator.isUnary() )
            throw new CIllegalArgumentException( CCommon.getLanguageString( IBaseUnary.class, "operator", p_operator ) );

        m_operator = p_operator;
        m_expression = p_expression;
    }

    @Override
    public final IExpression getExpression()
    {
        return m_expression;
    }

    @Override
    public final EOperator getOperator()
    {
        return m_operator;
    }

    @Override
    public final double score( final IAgent p_agent )
    {
        return 0;
    }

    @Override
    public final int hashCode()
    {
        return m_expression.hashCode() + m_operator.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "{0}({1})", m_operator, m_expression );
    }

    @Override
    public final Stream<IVariable<?>> getVariables()
    {
        return m_expression.getVariables();
    }

}
