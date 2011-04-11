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
        float similaridade = 1;
        similaridade += SimilarNode.ELEMENT_NAME_WEIGTH;
        similaridade += SimilarNode.ELEMENT_VALUE_WEIGTH;
        similaridade += SimilarNode.ATTRIBUTE_WEIGTH;

        assertTrue(similaridade > 1);


        SimilarNode similarNode = createSimilarNode("<tag attribute='yes'>Texto</tag>");

        assertEquals(1, similarNode.similar(similarNode).getSimilarity(), 1);
    }

    @Test
    public void similaridadeEntreNomes() {
        SimilarNode similarNode = createSimilarNode("<igual/>");

        assertEquals(SimilarNode.ELEMENT_NAME_WEIGTH, similarNode.elementsNameSimilarity(similarNode.getNode()), 0);
    }

    @Test
    public void similaridadeEntreNomes2() {
        SimilarNode similarNode = createSimilarNode("<igual></igual>");

        assertEquals(SimilarNode.ELEMENT_NAME_WEIGTH, similarNode.elementsNameSimilarity(similarNode.getNode()), 0);
    }

    @Test
    public void similaridadeEntreNomes3() {
        SimilarNode similarNode = createSimilarNode("<diferente/>");
        SimilarNode similarNode2 = createSimilarNode("<diferente2/>");

        assertEquals(0, similarNode.elementsNameSimilarity(similarNode2.getNode()), 0);
    }

    @Test
    public void similaridadeEntreConteudoDasTags() {
        SimilarNode similarNode = createSimilarNode("<tag>Texto</tag>");

        assertEquals(SimilarNode.ELEMENT_VALUE_WEIGTH, similarNode.elementsValueSimilarity(similarNode.getNode()), 0);
    }

    @Test
    public void similaridadeEntreConteudoDasTags2() {
        SimilarNode similarNode = createSimilarNode("<tag>Text1</tag>");
        SimilarNode similarNode2 = createSimilarNode("<tag>Text2</tag>");

        assertEquals(0.8 * SimilarNode.ELEMENT_VALUE_WEIGTH, similarNode.elementsValueSimilarity(similarNode2.getNode()), 0.01);
    }

    @Test
    public void similaridadeEntreConteudoDasTags3() {
        SimilarNode similarNode = createSimilarNode("<tag>Texto</tag>");
        SimilarNode similarNode2 = createSimilarNode("<tag></tag>");

        assertEquals(0, similarNode.elementsValueSimilarity(similarNode2.getNode()), 0);
    }

    @Test
    public void similaridadeEntreConteudoDasTags4() {
        SimilarNode similarNode = createSimilarNode("<tag></tag>");
        SimilarNode similarNode2 = createSimilarNode("<tag>Texto</tag>");

        assertEquals(0, similarNode.elementsValueSimilarity(similarNode2.getNode()), 0);
    }

    @Test
    public void similaridadeEntreAtributos() {
        SimilarNode similarNode = createSimilarNode("<igual atributo='sim'></igual>");
        SimilarNode similarNode2 = createSimilarNode("<igual></igual>");

        assertEquals(0, similarNode.elementsAttributesSimilarity(similarNode2.getNode()), 0);
    }

    @Test
    public void similaridadeEntreAtributos2() {
        SimilarNode similarNode = createSimilarNode("<igual></igual>");
        SimilarNode similarNode2 = createSimilarNode("<igual atributo='sim'></igual>");

        assertEquals(0, similarNode.elementsAttributesSimilarity(similarNode2.getNode()), 0);
    }

    @Test
    public void similaridadeEntreAtributos3() {
        SimilarNode similarNode = createSimilarNode("<igual atributo='sim'></igual>");

        assertEquals(SimilarNode.ATTRIBUTE_WEIGTH, similarNode.elementsAttributesSimilarity(similarNode.getNode()), 0);
    }

    @Test
    public void similaridadeEntreAtributos4() {
        SimilarNode similarNode = createSimilarNode("<igual atributo='nao'></igual>");
        SimilarNode similarNode2 = createSimilarNode("<igual atributo='sim'></igual>");

        assertEquals(0, similarNode.elementsAttributesSimilarity(similarNode2.getNode()), 0);
    }

    @Test
    public void similaridadeEntreAtributos5() {
        SimilarNode similarNode = createSimilarNode("<igual atributo='nao'></igual>");
        SimilarNode similarNode2 = createSimilarNode("<igual atributo='nao' outro='sim'></igual>");

        assertEquals(SimilarNode.ATTRIBUTE_WEIGTH / 2, similarNode.elementsAttributesSimilarity(similarNode2.getNode()), 0);
    }

    @Test
    public void similaridadeEntreFilhosDoElemento() {
        SimilarNode similarNode = createSimilarNode("<father><son></son></father>");
        SimilarNode similarNode2 = createSimilarNode("<father><son></son></father>");

        assertEquals(SimilarNode.ELEMENT_CHILDREN_WEIGTH, similarNode.elementsChildrenSimilarity(similarNode2.getNode()), 0);
    }

    @Test
    public void similaridadeEntreFilhosDoElemento2() {
        SimilarNode similarNode = createSimilarNode("<father><son></son></father>");
        SimilarNode similarNode2 = createSimilarNode("<father><son2></son2></father>");

        assertEquals(0, similarNode.elementsChildrenSimilarity(similarNode2.getNode()), 0);
    }

    @Test
    public void similaridadeEntreFilhosDoElemento3() {
        SimilarNode similarNode = createSimilarNode("<father><son></son><a/><b/><c/><d/><e/></father>");
        SimilarNode similarNode2 = createSimilarNode("<father><son></son><a/><b/><c/><d/><e/></father>");

        assertEquals(SimilarNode.ELEMENT_CHILDREN_WEIGTH, similarNode.elementsChildrenSimilarity(similarNode2.getNode()), 0.01);
    }

    //    @Test
    public void similaridadeEntreFilhosDoElemento4() {
        SimilarNode similarNode = createSimilarNode("<teste><nome>Fernando</nome><cpf>123</cpf><idade></idade><as></as><bs></bs><cidade></cidade></teste>");
        SimilarNode similarNode2 = createSimilarNode("<teste><nome>Fernando</nome><cpf>123</cpf><idade></idade><cidade></cidade></teste>");

        assertEquals(1, similarNode.elementsChildrenSimilarity(similarNode2.getNode()), 0.01);
    }

    @Test
    public void similaridadeEntreFilhosDoElementoNaoDeveriaContarElementosSemSerDoTipoElementNode() {
        SimilarNode similarNode = createSimilarNode("<father>Texto</father>");
        SimilarNode similarNode2 = createSimilarNode("<father>Texto<son></son></father>");

        assertEquals(0, similarNode.elementsChildrenSimilarity(similarNode2.getNode()), 0);
    }

    @Test
    public void deleteThisTest() {
        SimilarNode similarNode = createSimilarNode("<father><son><grandson attribute='yes'/></son></father>");
        SimilarNode similarNode2 = createSimilarNode("<father><son><grandson attribute='no'/></son><anotherson/></father>");

        similarNode.similar(similarNode2);

        System.out.println("Fim do teste.");
    }
}
