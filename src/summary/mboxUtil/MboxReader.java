
/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package summary.mboxUtil;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.SingleBody;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.dom.address.Address;
import org.apache.james.mime4j.mboxiterator.CharBufferWrapper;
import org.apache.james.mime4j.mboxiterator.MboxIterator;
import org.apache.james.mime4j.message.AbstractEntity;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.stream.MimeConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import summary.structure.*;
import summary.structure.Thread;

/**
 * Simple example of how to use Apache Mime4j Mbox Iterator. We split one mbox file file into
 * individual email messages.
 */
public class MboxReader {

    private final static CharsetEncoder ENCODER = Charset.forName("UTF-8").newEncoder();
    private final static int MAXLINELEN = 10000;
    public final static boolean DEBUG = true;
	private static class EmailWrapper {
		private String subject;
		private Email email;

		public EmailWrapper(String sub, Email em){
    			subject = sub;
    			email = em;
    		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public Email getEmail() {
			return email;
		}

		public void setEmail(Email email) {
			this.email = email;
		}
	}

    // simple example of how to split an mbox into individual files
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Please supply a path to a mbox file to parse");
            return;
        }
        
		for (Thread th : parseThreads(args)) {
			System.out.println(th.getEmailNumber() + "\t" + th.getHeader());
//			System.out.println(th.toString());
			for (Email em : th.getEmails()) {
//				System.out.println(em.getSendBy() + "-->" + em.getSendTo());
				for (Sentence s : em.getSentences()) {
					System.out.println(s.getQuotationTimes() + ", "
							+ s.getText());
				}
			}
			if(DEBUG) break;
		}
    }
    public static ArrayList<Thread> parseThreads(String[] mboxes) 
    		throws FileNotFoundException, IOException, MimeException{
   
//		long start = System.currentTimeMillis();
		int count = 0;
		ArrayList<Thread> threads = new ArrayList<Thread>();
		HashMap<String, Thread> threadMap = new HashMap<String, Thread>();
		Thread thread = null;
		
		for (String mb : mboxes) {
			final File mbox = new File(mb);
			for (CharBufferWrapper message : MboxIterator.fromFile(mbox)
					.charset(ENCODER.charset()).build()) {

				EmailWrapper emp = messageSummary(message.asInputStream(ENCODER.charset()));
				
				if(emp == null) continue;
				if(thread == null) thread = new Thread(emp.getSubject(), 0);

				// handle new threads
				// covers only the simple situation where replier adds info to the subject
				if(!emp.getSubject().contains(thread.getHeader())
						&& !thread.getHeader().contains(emp.getSubject())){
					// merge interrupted threads
					//threads.add(thread);
					if(threadMap.containsKey(thread.getHeader())){
						Thread oldThread = threadMap.get(thread.getHeader());
						for(Email em : thread.getEmails()) oldThread.addEmail(em);
					}
					else threadMap.put(thread.getHeader(), thread);
					
					thread = new Thread(emp.getSubject(), 0);
					thread.addEmail(emp.getEmail());
				}
				else{
					thread.addEmail(emp.getEmail());	
				}
				count++;
			}
		}
//		System.out.println("Found " + count + " messages");
//        long end = System.currentTimeMillis();
//        System.out.println("Done in: " + (end - start) + " milis");
        
		if(thread != null){
			if(threadMap.containsKey(thread.getHeader())){
				Thread oldThread = threadMap.get(thread.getHeader());
				for(Email em : thread.getEmails()) oldThread.addEmail(em);
			}
			else threadMap.put(thread.getHeader(), thread);
		}
		
		threads.addAll(threadMap.values());
		return threads;
    }



    /**
     * Parse a message and return a simple {@link String} representation of some important fields.
     *
     * @param messageBytes the message as {@link java.io.InputStream}
     * @return String
     * @throws IOException
     * @throws MimeException
     */
	private static EmailWrapper messageSummary(InputStream messageBytes)
			throws IOException, MimeException {
		try {
			DefaultMessageBuilder builder = new DefaultMessageBuilder();
			
			MimeConfig.Builder b = MimeConfig.custom();
			b.setMaxLineLen(MAXLINELEN);
			builder.setMimeEntityConfig(b.build());

			Message message = builder.parseMessage(messageBytes);

			// Here assume there's only one sender
			Email email = new Email(message.getFrom().get(0).getAddress(),
					message.getDate().toString());
			for (Address addr : message.getTo()) {
				email.addSendTo(addr.toString());
			}
			if (message.getCc() != null) {
				for (Address addr : message.getCc()) {
					email.addSendTo(addr.toString());
				}
			}
			if (message.getBcc() != null) {
				for (Address addr : message.getBcc()) {
					email.addSendTo(addr.toString());
				}
			}

			ArrayList<Sentence> ss = parsePhrases(
					parseBodyParts((Multipart) message.getBody()));
			for (Sentence s : ss)
				email.addSentence(s);
			String subject = message.getSubject();
			if(subject.startsWith("Re:")) 
				subject = subject.replace("Re:", "").trim();
			
			return new EmailWrapper(subject, email);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public static ArrayList<Sentence> parsePhrases(String embody) {
		// split by empty lines and >+ only in the line
		ArrayList<Sentence> sentences = new ArrayList<Sentence>();
		String[] lines = embody.split("\n"); 
		int quoteLevel = 0;
		StringBuilder phrase = new StringBuilder();

		for(String line : lines){

			int angles = countAngles(line);
			line = line.substring(angles).trim();
			if(line.isEmpty()) continue;
			line += " "; // add a whitespace to the end because we trimmed it before
			
			if(line.matches("(.*On[ \\w,]+at[ \\w,:]+[AP]M.*)|.*wrote:.*") ) {
				continue;
			}
//			System.out.println(angles  + "--||" +line );
//			System.out.println(line);

			//TODO: should remove all "On Wed, Sep 25, 2013 at 10:37 AM, Christopher Connors <
			// christopher.n.connors@gmail.com> wrote:"
			
			if(angles == quoteLevel && !line.isEmpty()){
				phrase.append(line);
			}
			else if(angles >= quoteLevel){
				if(phrase.length() > 0){
//					System.out.println(quoteLevel + ", " + phrase);
					sentences.addAll(parseSentences(phrase.toString(), quoteLevel));
					phrase = new StringBuilder();
					quoteLevel = angles;
				}
				// +1 to skip the space between >>>[_]Let's do it.
				phrase.append(line);
			}
		}
		if(phrase.length() > 0){
//			System.out.println(quoteLevel + ", " + phrase);
			sentences.addAll(parseSentences(phrase.toString(), quoteLevel));
		}
		
		return sentences;
    }
	public static ArrayList<Sentence> parseSentences(String phrase, int quote){
		ArrayList<Sentence> listSen = new ArrayList<Sentence>();
		if(phrase == null) return listSen;
		
		// split phrases into sentences
//		String[] sentences = phrase.split("[.?!]");
//		for(String s : sentences) System.out.println(s);
//		for(String s : sentences){
//			listSen.add(new Sentence(quote, s));
//		}
		
		listSen.add(new Sentence(quote, phrase.replaceAll("^\\s*\\n", " ")));
		return listSen;
	}
	
	public static int countAngles(String str){
		int count = 0;
		boolean prev = false;
		for(int i = 0; i < str.length(); i++){
			if(i == 0){
				if(str.charAt(i) == '>') count++;
				else break;
			}
			else if(str.charAt(i-1) == '>' && str.charAt(i) == '>') count++;
			else break;
		}
		return count;
	}
    /**
    *
    * @author Denis Lunev <den@mozgoweb.com>
    */
    private static String getTxtPart(Entity part) throws IOException {
        //Get content from body
        TextBody tb = (TextBody) part.getBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        tb.writeTo(baos);
        return new String(baos.toByteArray());
    }
    /**
    *
    * @author Denis Lunev <den@mozgoweb.com>
    */
    private static String parseBodyParts(Multipart multipart) throws IOException {
    	StringBuffer txtBodies = new StringBuffer();
        for (Entity part : multipart.getBodyParts()) {
            if (((AbstractEntity) part).isMimeType("text/plain")) {
                String txt = getTxtPart(part);
                txtBodies.append(txt);
            }

            //If current part contains other, parse it again by recursion
            if (part.isMultipart()) {
                parseBodyParts((Multipart) part.getBody());
            }
        }
        return txtBodies.toString();
    }
    private static void saveMessageToFile(int count, CharBuffer buf) throws IOException {
        FileOutputStream fout = new FileOutputStream(new File("messages/msg-" + count));
        FileChannel fileChannel = fout.getChannel();
        ByteBuffer buf2 = ENCODER.encode(buf);
        
        fileChannel.write(buf2);
        fileChannel.close();
        fout.close();
    }
}