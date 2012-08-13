import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;
/*
 * InstrumentReader.java
 */

// Assumptions
// -

/**
 * Parses and outputs all contents of the XML file.
 * @author Alex
 *
 */
public class InstrumentReader extends DefaultHandler {
	
	// hierarchy holds the hierarchy of the XML file as the SAX parser
	// parses each line. In the endElement() function the hierarchy is
	// modified by moving one step back or up the hierarchy.
	private ArrayList< DefaultMutableTreeNode > hierarchy = new ArrayList< DefaultMutableTreeNode >();
	DefaultMutableTreeNode lastNode;
	
	private JTree xmlTree = null;
	XMLReader xreader;
	boolean anticipatingText;
	
	/**
	 * Constructor. Prepares XMLReader for parsing.
	 */
	public InstrumentReader() {
		xreader = null;
		
		try { 
    		xreader = XMLReaderFactory.createXMLReader();
    	}
    	catch ( SAXException e ) {
    		
    	}
    	
    	if ( xreader != null ) {
    		//handler = new ParseDiff(); // this is the parser
    		xreader.setContentHandler( this );
    		xreader.setErrorHandler( this );
    	}
	}
	
	/**
	 * Parses the XML file.
	 * @param filePath the path of the XML file
	 * @return JTree tree structure of the XML file
	 */
	public DefaultMutableTreeNode beginParse( String filePath ) {
    	FileReader r = null;
    	
		try 
		{
			r = new FileReader( filePath );
			xreader.parse( new InputSource( r ) );
		}
		catch( FileNotFoundException e ) {
			
		}
		catch( SAXException e ) {
			
		}
		catch( IOException e ) {
			
		}
	    
		return hierarchy.get( 0 );
    }
	
	
	@Override
	public void startElement( String uri, String name, 
			String qName, Attributes atts ) {
		anticipatingText = true;
		
		DefaultMutableTreeNode node = new DefaultMutableTreeNode( name );
		
		hierarchy.add( node );
	}
	
	@Override
	public void endElement (String uri, String name, String qName) {
		if ( hierarchy.size() <= 1 )
			return;
		System.out.println( name );
		
		// get a reference to the last node
		DefaultMutableTreeNode lastNode;
		lastNode = hierarchy.get( hierarchy.size() - 1 );
		
		// remove the last node. After removal means the last node is now
		// the parent node of the node that was just removed.
		hierarchy.remove( hierarchy.size() - 1 );
		
		// add the saved reference to the last node to its parent,
		// which is now the last node.
		hierarchy.get( hierarchy.size() - 1 ).add( lastNode );
	}
	
	@Override
	public void characters (char ch[], int start, int length) {
		
		//ignore any values starting with escape characters.  this indicates
        //the parser is not dealing with a leaf node, so the value in ch[] is
        //irrelevant
        //also: added blank space seeing as that some of the attributes had
        //values which were nothing but blank space.
        if (ch[start] == '\n' || ch[start] == '\t' || ch[start] == '\r'  || ch[start] == ' ')
            return;
        
        String value = "";
        
        for ( int i = start; i < start + length; i++ ) {
        	if ( ch[start] != '\n' && ch[start] != '\t' && ch[start] != '\r' )
        		value += ch[ i ];
        		value = value.trim();
        }
        
        DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode( value );
        hierarchy.get( hierarchy.size() - 1 ).add( valueNode );
	}
}