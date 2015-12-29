package gov.nist.healthcare.mu.mmserver;

import gov.nist.healthcare.mu.mmlib.MessageModifier;
import gov.nist.healthcare.mu.mmlib.util.SampleData;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.immunizationsoftware.dqa.transform.ModifyMessageRequest;
import org.immunizationsoftware.dqa.transform.ModifyMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */


@Controller
public class FrontController {
	
	private static final Logger logger = LoggerFactory.getLogger(FrontController.class);
	
	
	/**
	 * Extract path from a controller mapping. /controllerUrl/** => return matched **
	 * @param request incoming request.
	 * @return extracted path
	 */
	public static String extractPathFromPattern(final HttpServletRequest request){
		return request.getRequestURL().toString();
	}

	// since this application serves a different application, outputing the url with which it was called
	// so that the called application can further reference other functions using this string
	public static String allButLastURI(String url) {
		return url.substring(0, url.lastIndexOf('/'));
	}
	
	// This request returns the master tab
	@RequestMapping(value = "/ShowMasterDialog.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String showMasterDialog(ModelMap model, HttpServletRequest request) {
		// adding the sample message; we need to get this from the request eventually
		// same for element and occurrences
		model.addAttribute("originalMessage", SampleData.message.replace("\n","&#13;&#10"));
		model.addAttribute("profile", SampleData.escapeHTML(SampleData.profile).replace("\n","&#13;&#10"));
		model.addAttribute("option", SampleData.option);
		model.addAttribute("element", SampleData.element);
		model.addAttribute("occurrences", SampleData.occurrences);
				
		//model.addAttribute("uri", extractPathFromPattern(request) + ";");
		model.addAttribute("appuri", allButLastURI(extractPathFromPattern(request)));
		return "ShowMasterDialog";
	}	

	// This is called when the user presses the transform button
	@RequestMapping(value = "/ApplyCardinality.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String applyCardinality(ModelMap model, 
			@RequestParam String message, 
			@RequestParam(required = false, defaultValue = "") String profile,
			@RequestParam(required = false, defaultValue = "") String element,
			@RequestParam(required = false, defaultValue = "") String option,
			@RequestParam(required = false, defaultValue = "") String occurrences,
			@RequestParam(required = false, defaultValue = "") String datasource,
			@RequestParam(required = false, defaultValue = "") String custdata,
			HttpServletRequest request) {
		MessageModifier m = new MessageModifier();
		try {
			//System.out.println(profile);
			message = m.setElementFromDataSource(message, profile, "CARD", option, element, occurrences, datasource, custdata);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("value", message);
		return "HtmlWithAValueDiv"; //msgR;
	}	

	
	/*******************************************************************************************
	 * Test methods/not being used currently
	 * @param model
	 * @param message
	 * @param transform
	 * @return
	 */
	@RequestMapping(value = "/TestAction", method = {RequestMethod.GET, RequestMethod.POST})
	public String testAction(ModelMap model, @RequestParam(required = false, defaultValue = "") String message, 
				@RequestParam(required = false, defaultValue = "") String transform) {
				
		transform.trim();
		ModifyMessageService m = new ModifyMessageService();
		ModifyMessageRequest req = new ModifyMessageRequest();
		
		req.setMessageOriginal(message);
		req.setTransformScript(transform);
		m.transform(req);
		
		model.addAttribute("originalMessage", req.getMessageOriginal().trim());
		model.addAttribute("transformScript", req.getTransformScript().trim() );
		model.addAttribute("transformedMessage", req.getMessageFinal().trim() );
		
		return "Home";
	}
	

	// once we replace the below request to an ajax call, we can return this object
	class MessageReturn {
		String uri;
		String message;
	}

	
	
	@RequestMapping(value = "/ApplyCardinality.json", method = RequestMethod.POST, produces="application/json", headers="Accept=application/json")
	public @ResponseBody String testMessage1(ModelMap model, 
			@RequestParam(required = false, defaultValue = "") String message,
			@RequestParam(required = false, defaultValue = "") String element,
			@RequestParam(required = false, defaultValue = "") String occurences,
			@RequestParam(required = false, defaultValue = "") String datasource,
			@RequestParam(required = false, defaultValue = "") String custdata,
			HttpServletRequest request) {
		MessageReturn msgR = new MessageReturn();
		MessageModifier m = new MessageModifier();
		//msgR.message = m.transformMessageUsingScript(message, "MSH-2=HelloModified");
		
		for (String s: m.getDataFromDataSource(element, occurences, datasource, custdata))
			msgR.message = m.setElementInMessage(message, element, s);
		return "abcd"; //msgR;
	}	

	
	
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "Home";
	}
	
	
	
	
	class Message {
		String uri;
		String value;
	}

		
	@RequestMapping(value = "/TestMessage/{message}", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Message testMessage(@PathVariable String message, HttpServletRequest request) {
		Message msg = new Message();
		msg.uri = extractPathFromPattern(request) + ";";
		msg.value = " This is the content of the messge";
		return msg;
	}	
	
}
