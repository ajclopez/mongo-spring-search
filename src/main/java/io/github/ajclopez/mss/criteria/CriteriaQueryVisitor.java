package io.github.ajclopez.mss.criteria;

import io.github.ajclopez.mss.QueryBaseVisitor;
import io.github.ajclopez.mss.model.CastType;
import io.github.ajclopez.mss.model.LogicalOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Map;

/**
 * 
 * This class provides an implementation of {@link QueryBaseVisitor}, which allows visiting the tree of logical operations and return MongoDB queries.
 *
 */
public class CriteriaQueryVisitor extends QueryBaseVisitor<Criteria> {

	private final Map<String, CastType> casters;
	
	public CriteriaQueryVisitor(Map<String, CastType> casters) {
		this.casters = casters;
	}
	
	@Override
	public Criteria visitInput(io.github.ajclopez.mss.QueryParser.InputContext ctx) {
		return super.visit(ctx != null ? ctx.query() : null);
	}
	
	@Override
	public Criteria visitPriorityQuery(io.github.ajclopez.mss.QueryParser.PriorityQueryContext ctx) {
		return super.visit(ctx != null ? ctx.query() : null);
	}
	
	@Override
	public Criteria visitAtomQuery(io.github.ajclopez.mss.QueryParser.AtomQueryContext ctx) {
		return super.visit(ctx != null ? ctx.criteria() : null);
	}
	
	@Override
	public Criteria visitOpQuery(io.github.ajclopez.mss.QueryParser.OpQueryContext ctx) {
	
		Criteria left = visit(ctx != null ? ctx.left : null);
		Criteria right = visit(ctx != null ? ctx.right : null);

		String logicalOperation = null;
		
		if (ctx != null && ctx.logicalOp != null ) {
			logicalOperation = ctx.logicalOp.getText();
		}
		
		if ( logicalOperation == null ) {
			return new Criteria().andOperator(left, right);
		}

        return switch (LogicalOperation.getLogicalOperation(logicalOperation)) {
            case OR -> new Criteria().orOperator(left, right);
            default -> new Criteria().andOperator(left, right);
        };
	}
	
	@Override
	public Criteria visitCriteria(io.github.ajclopez.mss.QueryParser.CriteriaContext ctx) {

		if ( ctx == null ) {
			throw new NullPointerException("Nullpointer CriteriaContext");
		}
		
		io.github.ajclopez.mss.QueryParser.KeyContext keyContext = ctx.key();
		if ( keyContext == null ) {
			throw new NullPointerException("Nullpointer keyContext");
		}
		
		io.github.ajclopez.mss.QueryParser.OpContext opContext = ctx.op();
		if ( opContext == null ) {
			throw new NullPointerException("Nullpointer opContext");
		}
		
		io.github.ajclopez.mss.QueryParser.ValueContext valueContext = ctx.value();
		if ( valueContext == null ) {
			throw new NullPointerException("Nullpointer valueContext");
		}
		
		String expression = String.format("%s%s%s", keyContext.getText().trim(), opContext.getText().trim(), valueContext.getText().trim());
		expression = expression.replace("+", "%2B");
		io.github.ajclopez.mss.model.SearchCriteria criteria = io.github.ajclopez.mss.parser.QueryParser.criteriaParser(expression, casters);
		
		return CriteriaImpl.buildCriteria(criteria);
	}
	
}
