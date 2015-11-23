package lamdafu.planner;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.ObjectUtils;

import lamdafu.metamodel.MetaField;
import lamdafu.metamodel.MetaLamda;

public class DAG {

	private static Comparator<MetaLamda> LAMDA_COMP = new Comparator<MetaLamda>() {
		@Override
		public int compare(MetaLamda a, MetaLamda b) {
			return ObjectUtils.compare(a.name, b.name);
		}
	};

	private static Comparator<MetaField> FIELD_COMP = new Comparator<MetaField>() {
		@Override
		public int compare(MetaField a, MetaField b) {
			return ObjectUtils.compare(a.name, b.name);
		}
	};

	List<MetaLamda> metamodel;
	List<MetaField> fields;

	protected DAG() {
		super();
	}

	public static DAG get() {
		return new DAG();
	}

	public DAG with(List<MetaLamda> metamodel) {
		this.metamodel = metamodel;
		SortedSet<MetaField> fieldSet = new TreeSet<>(FIELD_COMP);
		for (MetaLamda s : metamodel) {
			fieldSet.addAll(s.groupBy);
			fieldSet.addAll(s.input);
			fieldSet.addAll(s.output);
		}
		fields.addAll(fieldSet);
		return this;
	}

	/**
	 * Plan a DAG to pull data specified by the lamda. This would be the usual
	 * way to plan a SQL query for example.
	 * 
	 * @param metamodel
	 * @return
	 */
	public DAG pull(MetaLamda lamda) {
		
		return this;
	}
}
