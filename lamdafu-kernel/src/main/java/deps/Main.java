package deps;

import java.util.List;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.util.SqlShuttle;
import org.apache.calcite.sql.validate.SqlScopedShuttle;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Frameworks.ConfigBuilder;
import org.apache.calcite.tools.Programs;
import org.apache.calcite.tools.RelBuilder;

public class Main {

	public static void main(String[] args) throws Exception {
		SqlNode q = SqlParser.create("select a,b,c from blibblab x, booboo y, fooboo z where x.a = y.a and y.b = z.b and z.c between 1 and 3").parseQuery();
		q.accept(new MyVisitor());
	}

	public static class MyVisitor extends SqlShuttle {

		@Override
		public SqlNode visit(SqlLiteral literal) {
			System.out.println("literal " + literal);
			return super.visit(literal);
		}

		@Override
		public SqlNode visit(SqlIdentifier id) {
			System.out.println("id " + id);
			return super.visit(id);
		}

		@Override
		public SqlNode visit(SqlDataTypeSpec type) {
			System.out.println("type " + type);
			return super.visit(type);
		}

		@Override
		public SqlNode visit(SqlDynamicParam parm) {
			System.out.println("parm " + parm);
			return super.visit(parm);
		}

		@Override
		public SqlNode visit(SqlIntervalQualifier intervalQualifier) {
			System.out.println("intervalQualifier " + intervalQualifier);
			return super.visit(intervalQualifier);
		}

		@Override
		public SqlNode visit(SqlCall call) {
			System.out.println("call " + call.getKind() + ", n=" + call.operandCount());
			return super.visit(call);
		}

		@Override
		public SqlNode visit(SqlNodeList nodeList) {
			System.out.println("nodeList " + nodeList + " n=" + nodeList.size());
			return super.visit(nodeList);
		}

	}

}