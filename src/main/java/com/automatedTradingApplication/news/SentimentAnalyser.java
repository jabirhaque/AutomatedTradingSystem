package com.automatedTradingApplication.news;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import lombok.extern.slf4j.Slf4j;
import org.ejml.simple.SimpleMatrix;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service
public class SentimentAnalyser {

    private final StanfordCoreNLP pipeline;

    public SentimentAnalyser() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public double analyzeSentiment(String text) {
        //log.info("Calculating sentiment for text: " + text);
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);
        double result = 0;
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            SimpleMatrix sentimentMatrix = RNNCoreAnnotations.getPredictions(tree);
            double[] sentimentProbabilities = new double[sentimentMatrix.getNumElements()];
            for (int i = 0; i < sentimentMatrix.getNumElements(); i++) {
                sentimentProbabilities[i] = sentimentMatrix.get(i);
            }
            result += sentimentProbabilities[4] + sentimentProbabilities[3]/2 - sentimentProbabilities[1]/2 - sentimentProbabilities[0];
        }
        //log.info("Sentiment calculated: " + String.valueOf(result));
        return result;
    }
}