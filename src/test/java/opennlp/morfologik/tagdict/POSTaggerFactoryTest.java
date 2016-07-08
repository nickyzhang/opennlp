/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.morfologik.tagdict;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import opennlp.morfologik.builder.POSDictionayBuilderTest;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.TagDictionary;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelType;

import org.junit.Test;

/**
 * Tests for the {@link POSTaggerFactory} class.
 */
public class POSTaggerFactoryTest {

  private static ObjectStream<POSSample> createSampleStream()
      throws IOException {
    InputStream in = POSTaggerFactoryTest.class.getClassLoader()
        .getResourceAsStream("AnnotatedSentences.txt");

    return new WordTagSampleStream((new InputStreamReader(in)));
  }

  static POSModel trainPOSModel(ModelType type, POSTaggerFactory factory)
      throws IOException {
    return POSTaggerME.train("en", createSampleStream(),
        TrainingParameters.defaultParams(), factory);
  }

  @Test
  public void testPOSTaggerWithCustomFactory() throws Exception {

    Path dictionary = POSDictionayBuilderTest.createMorfologikDictionary();
    POSTaggerFactory inFactory = new MorfologikPOSTaggerFactory();
    TagDictionary inDict = inFactory.createTagDictionary(dictionary.toFile());
    inFactory.setTagDictionary(inDict);

    POSModel posModel = trainPOSModel(ModelType.MAXENT, inFactory);

    POSTaggerFactory factory = posModel.getFactory();
    assertTrue(factory.getTagDictionary() instanceof MorfologikTagDictionary);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    posModel.serialize(out);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

    POSModel fromSerialized = new POSModel(in);

    factory = fromSerialized.getFactory();
    assertTrue(factory.getTagDictionary() instanceof MorfologikTagDictionary);
  }

}