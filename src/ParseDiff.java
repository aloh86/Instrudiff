import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

//Assumptions: 
//- XML structure doesn't change between files, only values of innermost tags.
//- Only leafs have meaningful (and subject-to-change) text (i.e. non-escape characters).
//  This means mixing text values with child tags is not allowed!
//- Attributes are unimportant

//Problems:
//- Can't distinguish between different tags on the same level when presenting
//  the hierarchical view
//- Won't capture tags values with leading spaces but also meaningful content

/**
 * ParseDiff provides parsing of XML files. It detects changes in element
 * values between two XML files.
 * @author Taylor O'Brien
 *
 */
public class ParseDiff extends DefaultHandler
{
    //currStruct represents the current XML hierarchy (from the parsers) perspective
    //in string form.  uses tabs and tag names, e.g.:
    /*
     * currStruct.gt(0) = "     A"
     * currStruct.get(1) = "         B"
     * currStruct.get(2) = "              C"
     */
    public ArrayList<String> currStruct = new ArrayList<String>();
    //tagStruct stores currStructs in parallel with the tag values held in
    //tagVals1 and tagVals2.  for example, suppose the currStruct example above
    //is stored intagStruct(0), tagVals1(0) = "\t\t\t\t5", and tagVals2(0) = "8".  this
    //indicates that C's value changed from 5 to 8, and the entire XML structure
    //of this change can be seen.
    public ArrayList<ArrayList<String>> tagStruct = new ArrayList<ArrayList<String>>();
    //tagVals1 and 2 store the values of leaf nodes, in the same sequence
    //they appear in the document.  these structures work in parallel with each
    //other and with currStruct, as described above.
    public ArrayList<String> tagVals1 = new ArrayList<String>();
    public ArrayList<String> tagVals2 = new ArrayList<String>();
    //used to indicate where data should be stored, tagVals1 during the first
    //document's parsing, and tagVals2 on the second (or, as it stands, subsequent)
    //document's(') parsing.
    boolean firstParse = true;
    //used to handle differences in structure resulting from empty tags
    boolean anticipatingText = false;
    
    boolean badStructure = false;
    
    ArrayList< String > output;
    
    XMLReader xr;
    
    /**
     * Constructor.
     */
    public ParseDiff ()
    {
    	// prepare XMLReader object for parsing
    	xr = null;
    	output = new ArrayList< String >();
    	
    	try { 
    		xr = XMLReaderFactory.createXMLReader();
    	}
    	catch ( SAXException e ) {
    		
    	}
    	
    	if ( xr != null ) {
    		xr.setContentHandler( this );
    		xr.setErrorHandler( this );
    	}
    }
    
    /**
     * Parses XML file and returns the changes between the files
     * @param filePaths the paths to the files
     * @return ArrayList<String> the changes in element values. If
     * the structures of the XML files are not the same, an error
     * message is returned instead.
     */
    public ArrayList< String > beginParse( String [] filePaths ) {
    	FileReader r = null;
    	
    	// Parse each file provided on the command line.
		for ( int i = 0; i < filePaths.length; i++ ) {
			try {
				r = new FileReader( filePaths[ i ] );
				xr.parse( new InputSource( r ) );
			}
			catch( FileNotFoundException e ) {
				
			}
			catch( SAXException e ) {
				
			}
			catch( IOException e ) {
				
			}
		    
            if ( i > 0 ) {
                ArrayList< String > output = printDiff();
            	
                // if the structures are different, output an error message.
                // Other wise, return the output.
                if (tagVals1.size() != tagVals2.size())
            		badStructure = true;
            	
                if ( !badStructure )
                	return output;
                else if ( output == null ) {
                	output = new ArrayList< String >();
                	output.add( "ERROR: XML STRUCTURES ARE DIFFERENT" );
                	return output;
                }
            }
		}
		return null;
    }

    /**
     * Helper function for beginParse()
     * @return
     */
    private ArrayList< String > printDiff() {
    	//insanely messy, ugly, and unreadable code starts here!
        //the documents have been parsed, now the data needs to be compared,
        //changes noted, and results printed
        boolean firstDiff = false;
        int lastDiff = 0;
        
        if ( badStructure )
        	return null;
        //this loop steps through tagStruct, tagVals1, and tagVals2 concurrently,
        //looking for changes between the leaf nodes represented by tagVals1
        //and tagVals2.  if it finds any, it determines exactly how much of 
        //tagStruct to print out (giving a nice tree-like view of the changes, 
        //and then prints out the actual value changes
        for (int i = 0; i < tagStruct.size(); i++) {
            if (!tagVals1.get(i).trim().equals(tagVals2.get(i))) {
                //a change was detected
                int start = 0;
                //print off the tag structure only insofar as it differs from
                //the previously printed structure -- this gives the hierarchical
                //view of changes
                if (firstDiff) {
                    for (int k = 0; k < tagStruct.get(i).size(); k++) {
                        if (tagStruct.get(i).get(k).equals(tagStruct.get(lastDiff).get(k)))
                            start = k;
                        else
                            break;
                    }
                    start++;
                }
                firstDiff = true;
                lastDiff = i;
               
                String tag = "";
                String subTagsAndValChange = "";
                
                for(int j = start; j < tagStruct.get(i).size() - 1; j++)
                    tag = tagStruct.get(i).get(j) + '\n';
              
                subTagsAndValChange = tagStruct.get( i ).get( tagStruct.get( i ).size() - 1 );
                subTagsAndValChange += ":   " + tagVals1.get(i) + "  ->  ";
                subTagsAndValChange += tagVals2.get(i) + '\n';
                
                tag += subTagsAndValChange;
                
                output.add( tag );
            }
        }
        return output;
    }

    
    ////////////////////////////////////////////////////////////////////
    // Event handlers.
    ////////////////////////////////////////////////////////////////////

    //used to flip the firstParse flag, which signifies the parser should
    //add value-text strings to tagVals2, rather than tagVals1
    @Override
    public void endDocument ()
    {
	firstParse = false;
    }
    
    
    //adds an element to currstruct, thereby stepping into the XML hierarchy
    @Override
    public void startElement (String uri, String name,
			      String qName, Attributes atts)
    {
        anticipatingText = true;
        String s = "";
                
        for (int i = 0; i < currStruct.size(); i++)
            s += "   ";
        currStruct.add(s + qName);
    }

    //removes the last element from currStruct, thereby stepping up a level
    //in the XML hierarchy
    @Override
    public void endElement (String uri, String name, String qName)
    {
        if(anticipatingText)
            //in this case, we have an "empty tag" which is represented by "-"
            //in the output
            characters(new char[] {'-'}, 0, 1);
        
        currStruct.remove(currStruct.size() - 1);
    }

    //captures value text
    @Override
    public void characters (char ch[], int start, int length)
    {
        //ignore any values starting with escape characters.  this indicates
        //the parser is not dealing with a leaf node, so the value in ch[] is
        //irrelevant
        //also: added blank space seeing as that some of the attributes had
        //values which were nothing but blank space.
        if (ch[start] == '\n' || ch[start] == '\t' || ch[start] == '\r'  || ch[start] == ' ')
            return;
        
        //Because empty tags have the format <tagName /> (notice the extra space)
        //this check has to be after the above return condition in order to work
        //correctly
        anticipatingText = false;
        
        String s1 = "";
        String s2 = "";
        
        //add appropriate number of spaces
        int n = 40 - currStruct.get(currStruct.size() - 1).length();
        if (firstParse)
            for (int i = 0; i < n; i++)
                s1 += " ";
        
        //turn ch[] into string and trim trailing whitespace
        for (int i = start; i < start + length; i++)
            if (ch[start] != '\n' && ch[start] != '\t' && ch[start] != '\r')
                s2 += ch[i];
        s2 = s2.trim();
        
        //display format is " /t/t/t... tagVals1(i) -> tagVals2(i), so no tabs needed
        //for tagVals2
        if (firstParse) {
            tagVals1.add(s1 + s2);
            tagStruct.add(new ArrayList<String>(currStruct));
        } else {
            tagVals2.add(s2);
            if ( tagVals2.size() > tagVals1.size() || !tagStruct.get(tagVals2.size() - 1).equals(currStruct) )
            	badStructure = true;
        }
    }

}