/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.dialect.function.array;

import java.util.List;

import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.ArgumentTypesValidator;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionArgumentTypeResolvers;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.Expression;

import static org.hibernate.query.sqm.produce.function.FunctionParameterType.ANY;
import static org.hibernate.query.sqm.produce.function.FunctionParameterType.INTEGER;

/**
 * Implement the array get function by using {@code unnest}.
 */
public class ArrayGetUnnestFunction extends AbstractSqmSelfRenderingFunctionDescriptor {

	public ArrayGetUnnestFunction() {
		super(
				"array_get",
				StandardArgumentsValidators.composite(
						ArrayArgumentValidator.DEFAULT_INSTANCE,
						new ArgumentTypesValidator( null, ANY, INTEGER )
				),
				ElementViaArrayArgumentReturnTypeResolver.DEFAULT_INSTANCE,
				StandardFunctionArgumentTypeResolvers.invariant( ANY, INTEGER )
		);
	}

	@Override
	public void render(
			SqlAppender sqlAppender,
			List<? extends SqlAstNode> sqlAstArguments,
			SqlAstTranslator<?> walker) {
		final Expression arrayExpression = (Expression) sqlAstArguments.get( 0 );
		final Expression indexExpression = (Expression) sqlAstArguments.get( 1 );
		sqlAppender.append( "(select t.val from unnest(" );
		arrayExpression.accept( walker );
		sqlAppender.append( ") with ordinality t(val, idx) where t.idx=" );
		indexExpression.accept( walker );
		sqlAppender.append( ')' );
	}
}
