package gems.ic.uff.br.modelo.similar;

import org.xml.sax.InputSource;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;

import static org.junit.Assert.*;

public class SimilarNodeTest {

    public SimilarNodeTest() {
    }

    public SimilarNode createSimilarNode(String xml) {
        try {
            return new SimilarNode(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement());
        } catch (Exception ex) {
            System.out.println(ex.getClass() + ": " + ex.getMessage());
            return null;
        }
    }

    @Test
    public void similaridadeNoMaximoIgualA1() {
        float similaridade = 0;
        similaridade += SimilarNode.ELEMENT_NAME_WEIGTH;
        similaridade += SimilarNode.ELEMENT_VALUE_WEIGTH;
        similaridade += SimilarNode.ATTRIBUTE_WEIGTH;
        similaridade += SimilarNode.ELEMENT_CHILDREN_WEIGTH;

        assertTrue(similaridade == 1);
    }

    @Test
    public void igualdadeEntreNomes() {
        SimilarNode equalNode = createSimilarNode("<igual/>");
        float elementsNameSimilarity = equalNode.elementsNameSimilarity(equalNode.getNode());

        assertEquals(SimilarNode.ELEMENT_NAME_WEIGTH, elementsNameSimilarity, 0);
    }

    @Test
    public void desigualdadeEntreNomes() {
        SimilarNode node = createSimilarNode("<diferente/>");
        SimilarNode unequalNode = createSimilarNode("<diferente2/>");
        float elementsNameSimilarity = node.elementsNameSimilarity(unequalNode.getNode());

        assertEquals(0, elementsNameSimilarity, 0);
    }

    @Test
    public void igualdadeEntreConteudoDasTags() {
        SimilarNode equalNode = createSimilarNode("<tag>Texto</tag>");
        float elementsValueSimilarity = equalNode.elementsValueSimilarity(equalNode.getNode());

        assertEquals(SimilarNode.ELEMENT_VALUE_WEIGTH, elementsValueSimilarity, 0);
    }

    @Test
    public void similaridadeEntreConteudoDasTags() {
        SimilarNode node = createSimilarNode("<tag>Text1</tag>");
        SimilarNode similarNode = createSimilarNode("<tag>Text2</tag>");
        float elementsValueSimilarity = node.elementsValueSimilarity(similarNode.getNode());

        assertEquals((4.0/5.0) * SimilarNode.ELEMENT_VALUE_WEIGTH, elementsValueSimilarity, 0.01);
    }

    @Test
    public void desigualdadeEntreConteudoDasTags() {
        SimilarNode node = createSimilarNode("<tag>Texto</tag>");
        SimilarNode unequalNode = createSimilarNode("<tag></tag>");

        assertEquals(0, node.elementsValueSimilarity(unequalNode.getNode()), 0);
    }

    @Test
    public void desigualdadeEntreConteudoDasTags2() {
        SimilarNode node = createSimilarNode("<tag></tag>");
        SimilarNode unequalNode = createSimilarNode("<tag>Texto</tag>");

        assertEquals(0, node.elementsValueSimilarity(unequalNode.getNode()), 0);
    }
    
    
    @Test
    public void igualdadeEntreAtributos() {
        SimilarNode equalNode = createSimilarNode("<igual atributo='sim'></igual>");

        assertEquals(SimilarNode.ATTRIBUTE_WEIGTH, equalNode.elementsAttributesSimilarity(equalNode.getNode()), 0);
    }

    @Test
    public void desigualdadeEntreAtributos() {
        SimilarNode node = createSimilarNode("<igual atributo='sim'></igual>");
        SimilarNode unequalNode = createSimilarNode("<igual></igual>");

        assertEquals(0, node.elementsAttributesSimilarity(unequalNode.getNode()), 0);
    }

    @Test
    public void desigualdadeEntreAtributos2() {
        SimilarNode node = createSimilarNode("<igual></igual>");
        SimilarNode unequalNode = createSimilarNode("<igual atributo='sim'></igual>");

        assertEquals(0, node.elementsAttributesSimilarity(unequalNode.getNode()), 0);
    }

    @Test
    public void desigualdadeEntreAtributos3() {
        SimilarNode node = createSimilarNode("<igual atributo='nao'></igual>");
        SimilarNode unequalNode = createSimilarNode("<igual atributo='sim'></igual>");

        assertEquals(0, node.elementsAttributesSimilarity(unequalNode.getNode()), 0);
    }

    @Test
    public void similaridadeEntreAtributos() {
        SimilarNode node = createSimilarNode("<igual atributo='nao'></igual>");
        SimilarNode similarNode = createSimilarNode("<igual atributo='nao' outro='sim'></igual>");

        assertEquals(SimilarNode.ATTRIBUTE_WEIGTH / 2, node.elementsAttributesSimilarity(similarNode.getNode()), 0);
    }

    @Test
    public void igualdadeEntreFilhosDosElementos() {
        SimilarNode node = createSimilarNode("<father><son></son></father>");
        SimilarNode equalNode = createSimilarNode("<father><son></son></father>");

        assertEquals(SimilarNode.ELEMENT_CHILDREN_WEIGTH, node.elementsChildrenSimilarity(equalNode.getNode()), 0);
    }

    @Test
    public void desigualdadeEntreFilhosDosElementos() {
        SimilarNode node = createSimilarNode("<father><son></son></father>");
        SimilarNode unequalNode = createSimilarNode("<father><son2></son2></father>");

        assertEquals(0, node.elementsChildrenSimilarity(unequalNode.getNode()), 0);
    }

    @Test
    public void similaridadeEntreFilhosDosElementos() {
        SimilarNode node = createSimilarNode("<father><son></son><a/><b/><c/><d/><e/></father>");
        SimilarNode similarNode = createSimilarNode("<father><son></son><a/><b/><c/><d/><e/></father>");

        assertEquals(SimilarNode.ELEMENT_CHILDREN_WEIGTH, node.elementsChildrenSimilarity(similarNode.getNode()), 0.01);
    }
    
    @Test
    public void similaridadeEntreFilhosDosElementosNaoLevaEmConsideracaoOrdenacao() {
        SimilarNode node = createSimilarNode("<father><son></son><a/><b/><c/><d/><e/></father>");
        SimilarNode similarNode = createSimilarNode("<father><son></son><c/><a/><b/><e/><d/></father>");

        assertEquals(SimilarNode.ELEMENT_CHILDREN_WEIGTH, node.elementsChildrenSimilarity(similarNode.getNode()), 0.01);
    }

    @Test
    public void similaridadeEntreFilhosDosElementosNaoDeveriaContarElementosSemSerDoTipoElementNode() {
        SimilarNode node = createSimilarNode("<father atributo='valor'>Texto</father>");
        SimilarNode unequalNode = createSimilarNode("<father>Texto<son></son></father>");

        assertEquals(0, node.elementsChildrenSimilarity(unequalNode.getNode()), 0);
    }
}
