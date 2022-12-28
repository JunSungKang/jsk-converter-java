package kr.geneus.jskang.converter.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;

class XmlConvertTest {

    final String xml1 = "<note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>";
    final String xml2 = "<breakfast_menu><food><name>Belgian Waffles</name><price>$5.95</price><description>Two of our famous Belgian Waffles with plenty of real maple syrup</description><calories>650</calories></food><food><name>Strawberry Belgian Waffles</name><price>$7.95</price><description>Light Belgian waffles covered with strawberries and whipped cream</description><calories>900</calories></food><food><name>Berry-Berry Belgian Waffles</name><price>$8.95</price><description>Light Belgian waffles covered with an assortment of fresh berries and whipped cream</description><calories>900</calories></food><food><name>French Toast</name><price>$4.50</price><description>Thick slices made from our homemade sourdough bread</description><calories>600</calories></food><food><name>Homestyle Breakfast</name><price>$6.95</price><description>Two eggs, bacon or sausage, toast, and our ever-popular hash browns</description><calories>950</calories></food></breakfast_menu>";
    final String xml3 = "<project> <modelVersion>4.0.0</modelVersion> <parent> <groupId>com.mycompany.app</groupId> <artifactId>my-app</artifactId> <version>1</version> </parent> <groupId>com.mycompany.app11</groupId> <artifactId>my-module11</artifactId> <version>11</version> </project>";

    @Test
    void build1Success() throws ParserConfigurationException, IOException {
        XmlConvert xmlConvert = new XmlConvert();
        xmlConvert.setSource(this.xml1);
        xmlConvert.buildMap();
        assertThat(xmlConvert.toMap().toString())
            .isEqualTo("{note={to=Tove, from=Jani, heading=Reminder, body=Don't forget me this weekend!}}");

        xmlConvert.setSource(this.xml2);
        xmlConvert.buildMap();
        assertThat(xmlConvert.toMap().toString())
            .isEqualTo(
                "{breakfast_menu={food={name=Homestyle Breakfast, price=$6.95, description=Two eggs, bacon or sausage, toast, and our ever-popular hash browns, calories=950}}}");

        xmlConvert.setSource(this.xml3);
        xmlConvert.buildMap();
        assertThat(xmlConvert.toMap().toString())
            .isEqualTo(
                "{project={modelVersion=4.0.0, parent={groupId=com.mycompany.app, artifactId=my-app, version=1}, groupId=com.mycompany.app11, artifactId=my-module11, version=11}}");
    }

    @Test
    void build1Exception() throws ParserConfigurationException, IOException {
        XmlConvert xmlConvert = new XmlConvert();

        String xmlError1 = "<test><code>M</code>";
        xmlConvert.setSource(xmlError1);
        assertThatThrownBy(() -> xmlConvert.buildMap())
            .isInstanceOf(IOException.class);

        String xmlError2 = "testcodeM";
        xmlConvert.setSource(xmlError2);
        assertThatThrownBy(() -> xmlConvert.buildMap())
            .isInstanceOf(IOException.class);

        String xmlError3 = "<test><code><1>6>9</code></test>";
        xmlConvert.setSource(xmlError3);
        assertThatThrownBy(() -> xmlConvert.buildMap())
            .isInstanceOf(IOException.class);
    }

    @Test
    void build2Success() throws ParserConfigurationException, IOException {
        XmlConvert xmlConvert = new XmlConvert(this.xml1);
        xmlConvert.buildMap();
        assertThat(xmlConvert.toMap().toString())
            .isEqualTo("{note={to=Tove, from=Jani, heading=Reminder, body=Don't forget me this weekend!}}");

        xmlConvert = new XmlConvert(this.xml2);
        xmlConvert.buildMap();
        assertThat(xmlConvert.toMap().toString())
            .isEqualTo(
                "{breakfast_menu={food={name=Homestyle Breakfast, price=$6.95, description=Two eggs, bacon or sausage, toast, and our ever-popular hash browns, calories=950}}}");

        xmlConvert = new XmlConvert(this.xml3);
        xmlConvert.buildMap();
        assertThat(xmlConvert.toMap().toString())
            .isEqualTo(
                "{project={modelVersion=4.0.0, parent={groupId=com.mycompany.app, artifactId=my-app, version=1}, groupId=com.mycompany.app11, artifactId=my-module11, version=11}}");
    }
}