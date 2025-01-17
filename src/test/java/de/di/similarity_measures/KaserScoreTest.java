package de.di.similarity_measures;
import de.di.similarity_measures.helper.Tokenizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class KaserScoreTest {

    @Test
    public void testKaserScore() {
        Jaccard jaccard = null;
        double result = 0;
        KaserScore kaser = null;
        kaser = new KaserScore();

        result = kaser.calculate("", "");
        result = kaser.calculate("a", "a");

        result = kaser.calculate("jones", "johnson");
        result = kaser.calculate("Paul", "Pual");
        result = kaser.calculate("Paul Jones", "Jones, Paul");
        result = kaser.calculate("jones", "nnnnnnn");
        result = kaser.calculate("Prof._John_Doe", "Dr._John_Doe");
        result = kaser.calculate("Professor_John_Doe", "John_Doe");
        result = kaser.calculate("rnnnnn", "znnnnn");
        result = kaser.calculate("ren", "zinn");

        result = kaser.calculate("VL Big Data Systems 2020", "VL Big Data Integration 2022");
        result = kaser.calculate("Tim Tim Tina", "Tina Tim Tim");


//        result = kaser.calculate("shackleford", "shackelford");
//        result = kaser.calculate("nichleson", "nichluson");
//        result = kaser.calculate("massey", "massie");
//        result = kaser.calculate("jeraldine", "geraldine");
//        result = kaser.calculate("michelle", "michael");



//        assertEquals((double) 0.97, result, 0.000001);


        //jaccard = new Jaccard(new Tokenizer(2, false), true);
//        jaccard = new Jaccard(new Tokenizer(4, true), true);
//        result = jaccard.calculate("Tim Tim Tina", "Tina Tim Tim");
//        assertEquals((double) 7 / 30, result, 0.000001);
    }
}
