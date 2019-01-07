package io.rosenwald.springDemo.db;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.rosenwald.springDemo.db.VehicleRepository;
import io.rosenwald.springDemo.entities.Vehicle;
import io.rosenwald.springDemo.rest.VehicleRestController;

//TODO: Add more tests as more error handling is added to the REST controller.
@RunWith(SpringRunner.class)
@WebMvcTest(VehicleRestController.class)
public class VehicleRestControllerTest {
	
    @Autowired
    private MockMvc mvc;
	
	@MockBean
	private VehicleRepository repo;
	
	private static ObjectMapper mapper;
	
	private Vehicle countach;
	private Vehicle mustang;
	private Vehicle camaro;
	private Vehicle corvette;
	private Vehicle porsche918;
	private List<Vehicle> vehicleList;
	
	@BeforeClass
	public static void initAll() {
		mapper = new ObjectMapper();
	}
	
	@Before
	public void initTest() {
		countach = new Vehicle();
		countach.setYear(1988);
		countach.setMake("Lamborghini");
		countach.setModel("Countach");
		countach.setDrive("RWD");
		countach.setCylinders(12);
		countach.setDisplacement(4.8f);
		countach.setTransmission("Manual 5-spd");
		
		mustang = new Vehicle();
		mustang.setYear(2016);
		mustang.setMake("Ford");
		mustang.setModel("Mustang");
		mustang.setDrive("RWD");
		mustang.setCylinders(8);
		mustang.setDisplacement(5.0f);
		mustang.setTransmission("Manual 6-spd");
		
		camaro = new Vehicle();
		camaro.setYear(2016);
		camaro.setMake("Chevrolet");
		camaro.setModel("Camaro");
		camaro.setDrive("RWD");
		camaro.setCylinders(8);
		camaro.setDisplacement(6.2f);
		camaro.setTransmission("Manual 6-spd");
		
		corvette = new Vehicle();
		corvette.setYear(2016);
		corvette.setMake("Chevrolet");
		corvette.setModel("Corvette");
		corvette.setDrive("RWD");
		corvette.setCylinders(8);
		corvette.setDisplacement(6.2f);
		corvette.setTransmission("Manual 7-spd");
		
		porsche918 = new Vehicle();
		porsche918.setYear(2015);
		porsche918.setMake("Porsche");
		porsche918.setModel("918 Spyder");
		porsche918.setDrive("AWD");
		porsche918.setCylinders(8);
		porsche918.setDisplacement(4.6f);
		porsche918.setTransmission("Automatic (AM-S7)");
		porsche918.setEvMotor("95 kW and 116 kW DC Brushless");
		
		vehicleList = Arrays.stream(new Vehicle[] {countach, mustang, camaro, corvette, porsche918}).collect(Collectors.toCollection(ArrayList::new));
	}
	
	@Test
	public void getVehiclesByYearTest() throws Exception {
		List<Vehicle> expectedList = vehicleList.subList(1, 4);
		String expectedJson = mapper.writeValueAsString(expectedList);
	    when(repo.findByYear(mustang.getYear())).thenReturn(vehicleList.subList(1, 4));
	    
	    mvc.perform(get("/vehicles?year=" + mustang.getYear()))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	    
	    mvc.perform(get("/vehicles/" + mustang.getYear()))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void getVehiclesByMakeTest() throws Exception {
		List<Vehicle> expectedList = vehicleList.subList(2, 4);
		String expectedJson = mapper.writeValueAsString(expectedList);
	    when(repo.findByMake(camaro.getMake())).thenReturn(vehicleList.subList(2, 4));
	    
	    mvc.perform(get("/vehicles?make=" + corvette.getMake()))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void getVehicleByModelTest() throws Exception {
		List<Vehicle> expectedList = vehicleList.subList(4, 5);
		String expectedJson = mapper.writeValueAsString(expectedList);
	    when(repo.findByModel(porsche918.getModel())).thenReturn(vehicleList.subList(4, 5));
	    
	    mvc.perform(get("/vehicles?model=" + porsche918.getModel()))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void getVehiclesByYearAndMakeTest() throws Exception {
		List<Vehicle> expectedList = vehicleList.subList(2, 4);
		String expectedJson = mapper.writeValueAsString(expectedList);
	    when(repo.findByYearAndMake(camaro.getYear(), camaro.getMake())).thenReturn(vehicleList.subList(2, 4));
	    
	    mvc.perform(get("/vehicles?year=" + camaro.getYear() + "&make=" + camaro.getMake()))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	    
	    mvc.perform(get("/vehicles/" + camaro.getYear() + "/" + camaro.getMake()))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void getVehicleByYearAndMakeAndModelTest() throws Exception {
		List<Vehicle> expectedList = vehicleList.subList(3, 4);
		String expectedJson = mapper.writeValueAsString(expectedList);
	    when(repo.findByYearAndMakeAndModel(corvette.getYear(), corvette.getMake(), corvette.getModel())).thenReturn(vehicleList.subList(3, 4));
	    
	    mvc.perform(get("/vehicles?year=" + corvette.getYear() + "&make=" + corvette.getMake() + "&model=" + corvette.getModel()))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	    
	    mvc.perform(get("/vehicles/" + corvette.getYear() + "/" + corvette.getMake() + "/" + corvette.getModel())
	    		.contentType(MediaType.APPLICATION_JSON))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void getAllVehicles() throws Exception {
		String expectedJson = mapper.writeValueAsString(vehicleList);
	    when(repo.findAll()).thenReturn(vehicleList);
	    
	    mvc.perform(get("/vehicles"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void getVehiclesInvalidYear() throws Exception {
		String expectedJson = mapper.writeValueAsString(new Object[] {});
	    
	    mvc.perform(get("/vehicles?year=InvalidYear"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void getModelYears() throws Exception {
		List<Integer> years = new ArrayList<Integer>();
		vehicleList.forEach(vehicle -> years.add(vehicle.getYear()));
		Collections.sort(years);
		String expectedJson = mapper.writeValueAsString(years);
		when(repo.findAllYears()).thenReturn(years);
	    
	    mvc.perform(get("/modelYears"))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void postVehicle() throws Exception {
		String expectedJson = mapper.writeValueAsString(countach);
	    when(repo.save(any(Vehicle.class))).thenReturn(countach);
	    
	    mvc.perform(post("/vehicles")
	    		.content(expectedJson)
	    		.contentType(MediaType.APPLICATION_JSON)
	    		.with(csrf()))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void deleteVehicle() throws Exception {
		String expectedJson = mapper.writeValueAsString(countach);
	    when(repo.findById(anyString())).thenReturn(Optional.of(countach));
	    
	    mvc.perform(delete("/vehicles/" + countach.getId())
	    		.with(csrf()))
	    	.andExpect(status().isOk())
	    	.andExpect(content().json(expectedJson));
	}
	
	@Test
	public void deleteVehicleInvalidId() throws Exception {
	    when(repo.findById(anyString())).thenReturn(Optional.empty());
	    
	    mvc.perform(delete("/vehicles/INVALID-ID")
	    		.with(csrf()))
	    	.andExpect(status().isNoContent());
	}
}
