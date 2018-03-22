package com.fmning.wcservice.controller.mvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fmning.service.dao.impl.QueryTerm;
import com.fmning.service.temp.Cmain;
import com.fmning.service.temp.CmainDao;
import com.fmning.service.temp.Ecg;
import com.fmning.service.temp.EcgDao;
import com.fmning.service.temp.Ppg;
import com.fmning.service.temp.PpgDao;

@Controller
public class TempController {
	
	@Autowired private CmainDao mainDao;
	@Autowired private PpgDao ppgDao;
	@Autowired private EcgDao ecgDao;
	
	@RequestMapping(value = "/mqp", method = RequestMethod.GET)
    public String map(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		model.addAttribute("list", getAllList());
		
		return "temp";
	}
	
	@RequestMapping(value = "/get_all_ppgecg", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllPpgEcg(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		List<Cmain> origList = getAllList();
		List<Map<String, Object>> theList = new ArrayList<Map<String, Object>>();
		
		for (Cmain c : origList) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", c.getId());
			map.put("ehr", c.getEhr());
			map.put("phr", c.getPhr());
			map.put("temp", c.getTemp());
			map.put("spo2", c.getSpo2());
			map.put("createdAt", c.getCreatedAt().toString());
			theList.add(map);
		}
		
		respond.put("list", theList);
		respond.put("error", "");
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	private List<Cmain> getAllList() {
		try {
			return mainDao.findAllObjects();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/save_ppgecg")
	public ResponseEntity<Map<String, Object>> saveppgecg(@RequestBody Map<String, Object> request) {
		/* For example
		 {
			"ehr": 3,
			"phr": 6,
			"temp": 3.5,
			"spo2": 20,
			"ppg": [[1,2], [6,2], [7,6], [15,9], [26,4], [12,9], [17,18], [24,21], [27,9]],
			"ecg": [23, 44, 55, 43, 45, 59, 43, 34, 22, 28, 38, 37, 35, 42, 55, 43, 31, 20]
		}
		{
			"ehr": 7,
			"phr": 20,
			"temp": 1.8,
			"spo2": 99,
			"ppg": [[51,52], [56,52], [57,56], [65,69], [66,64], [42,49], [37,38], [64,61], [47,69]],
			"ecg": [93, 64, 55, 93, 85, 59, 43, 54, 62, 98, 88, 67, 75, 42, 55, 93, 71, 50]
		}
		 */
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			//Saving main obj
			Cmain c = new Cmain();
			c.setEhr((int)request.get("ehr"));
			c.setPhr((int)request.get("phr"));
			c.setTemp((double)request.get("temp"));
			c.setSpo2((int)request.get("spo2"));
			c.setCreatedAt(Instant.now());
			int mainId = mainDao.persist(c);
			
			//Saving the 150 PPG
			List<List<Integer>> ppgList = (List<List<Integer>>)request.get("ppg");
			for (List<Integer> p : ppgList) {
				Ppg ppg = new Ppg();
				ppg.setMid(mainId);
				ppg.setIrd(p.get(0));
				ppg.setRd(p.get(1));
				ppgDao.persist(ppg);
			}
			
			//Saving the 1000 ECG
			List<Integer> ecgList = (List<Integer>)request.get("ecg");
			for (int e : ecgList) {
				Ecg ecg = new Ecg();
				ecg.setMid(mainId);
				ecg.setEd(e);
				ecgDao.persist(ecg);
			}
			
			respond.put("error", "");
		}catch(Exception e){
			e.printStackTrace();
			respond = new HashMap<String, Object>();
			respond.put("error", e.getLocalizedMessage());
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/get_graph_data", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getGraphData(HttpServletRequest request) {
		Map<String, Object> respond = new HashMap<String, Object>();
		try{
			int id = Integer.parseInt(request.getParameter("id"));
			List<QueryTerm> values = new ArrayList<QueryTerm>();
			values.add(PpgDao.Field.MID.getQueryTerm(id));
			
			List<Ppg> ppgList = ppgDao.findAllObjects(values);
			List<Integer> irdList = new ArrayList<>();
			List<Integer> rdList = new ArrayList<>();
			for (Ppg p : ppgList) {
				irdList.add(p.getIrd());
				rdList.add(p.getRd());
			}
			respond.put("irdList", irdList);
			respond.put("rdList", rdList);
			
			values = new ArrayList<QueryTerm>();
			values.add(EcgDao.Field.MID.getQueryTerm(id));
			
			List<Ecg> ecgList = ecgDao.findAllObjects(values);
			List<Integer> edList = new ArrayList<>();
			for (Ecg e : ecgList) {
				edList.add(e.getEd());
			}
			respond.put("edList", edList);
			respond.put("error", "");
			
		}catch(Exception e){
			e.printStackTrace();
			respond = new HashMap<String, Object>();
			respond.put("error", e.getLocalizedMessage());
		}
		return new ResponseEntity<Map<String, Object>>(respond, HttpStatus.OK);
	}

}
