package lamdafu.metamodel;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MetaModelJsonTest {

	@Test
	public void test() {
		Gson gson = new GsonBuilder().disableHtmlEscaping().generateNonExecutableJson()
				.serializeSpecialFloatingPointValues().disableInnerClassSerialization().setPrettyPrinting().create();
		MetaLamda lamda = new MetaLamda("lamda", 0.0d);
		System.out.println(gson.toJson(lamda));
		lamda.input.add(new MetaField("fact1_name", null));
		lamda.output.add(new MetaField("product1_name", null));
		String json = gson.toJson(lamda);
		System.out.println(gson.toJson(gson.fromJson(json, MetaLamda.class)));
	}

}
