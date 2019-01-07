package io.rosenwald.springDemo.db;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import io.rosenwald.springDemo.DemoApplication;
import io.rosenwald.springDemo.entities.Vehicle;

/**
 * Imports vehicle data from a CSV file provided by the U.S. Department of Energy. 
 * This component will run on application start up, but will not run during testing.
 * 
 * @author Nathaniel Rosenwald
 *
 */
@Component
@ConditionalOnProperty(prefix = "io.rosenwald.springDemo", value = "db.import", havingValue = "true", matchIfMissing = false)
public class VehicleDataImporter {

	@Autowired
	private VehicleRepository repository;
	
	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	//TODO: Apply much safer exception handling.
	@PostConstruct
	public void onStart() {
		CSVParser parser = null;
		try {
			parser = CSVParser.parse(new ClassPathResource("data/vehicles.csv").getFile(), Charset.forName("US-ASCII"), CSVFormat.EXCEL.withHeader());
		} catch (IOException ex) {
			logger.error("Could not find vehicle data for import." + System.lineSeparator() + ex.getMessage() + System.lineSeparator() + ex.getStackTrace());
			return;
		}
		int yearCol 	= parser.getHeaderMap().get("year");
		int makeCol 	= parser.getHeaderMap().get("make");
		int modelCol 	= parser.getHeaderMap().get("model");
		int driveCol 	= parser.getHeaderMap().get("drive");
		int tranyCol 	= parser.getHeaderMap().get("trany");
		int cylCol 		= parser.getHeaderMap().get("cylinders");
		int displCol 	= parser.getHeaderMap().get("displ");
		int altCol		= parser.getHeaderMap().get("atvType");
		int evCol 		= parser.getHeaderMap().get("evMotor");
		
		MultithreadedRepositoryCommunicator<Vehicle> communicator = new MultithreadedRepositoryCommunicator<Vehicle>(repository, 1000);
		try {
			communicator.start(repository.findAll(), MultithreadedRepositoryCommunicator.BatchAction.DELETE);
		} catch (IllegalArgumentException | IllegalStateException | InterruptedException ex) {
			// Swallow exception. 
		}
		
		Date start = new Date();
		List<Vehicle> vehiclesToStore = new ArrayList<Vehicle>();
		try {
			parser.getRecords().parallelStream().forEach(record -> {
				if (record.get(yearCol) != null && !record.get(yearCol).isEmpty() && record.get(makeCol) != null && 
						!record.get(makeCol).isEmpty() && record.get(modelCol) != null && !record.get(modelCol).isEmpty()) {
					try {
						int year = NumberUtils.parseNumber(record.get(yearCol), Integer.class);
						String make = record.get(makeCol);
						String model = record.get(modelCol);
						String drive = record.get(driveCol);
						String transmission = record.get(tranyCol);
						int cylinders = NumberUtils.parseNumber(record.get(cylCol), Integer.class);
						float displacement = NumberUtils.parseNumber(record.get(displCol), Float.class);
						String altType = record.get(altCol);
						String evMotor = record.get(evCol);
						
						vehiclesToStore.add(new Vehicle(year, make, model, drive, transmission, cylinders, displacement, altType, evMotor));
					} catch (NumberFormatException ex) {
						// Do nothing, don't store the vehicle data if the year, cylinders, or displacement are improper. 
						// Only want complete numeric data.
					}
				}
			});
		} catch (IOException ex) {
			logger.error("Failed to parse the CSV data." + System.lineSeparator() + ex.getMessage() + System.lineSeparator() + ex.getStackTrace());
		}
		logger.debug("CSV parsing took " + (new Date().getTime() - start.getTime()) + "ms");
		vehiclesToStore.removeIf(Objects::isNull);
		logger.info("Saving " + vehiclesToStore.size() + " vehicle records to database...");
		
		//Multi-threaded, fixed size batching.
		try {
			communicator.start(vehiclesToStore, MultithreadedRepositoryCommunicator.BatchAction.SAVE);
		} catch (IllegalArgumentException | IllegalStateException | InterruptedException ex) {
			logger.error("Failed to persist all CSV vehicle data." + System.lineSeparator() + ex.getMessage() + System.lineSeparator() + ex.getStackTrace());
		}
		
		//Single-thread, fixed size batching.
//		List<Vehicle> queue = new ArrayList<Vehicle>();
//		AtomicInteger savedBatches = new AtomicInteger();
//		vehiclesToStore.forEach(vehicle -> {
//			queue.add(vehicle);
//			if (queue.size() == 100) {
//				repository.saveAll(queue);
//				logger.debug(savedBatches.incrementAndGet()*100 + "vehicles saved.");
//				queue.clear();
//			}
//		});
//		
		
		//Parallel queuing, Single-threaded persisting, no batching.
//		AtomicInteger count = new AtomicInteger(vehiclesToStore.size());
//		vehiclesToStore.parallelStream().forEach(vehicle -> {
//			repository.save(vehicle);
//			if ((count.get() % 20) == 0) logger.debug(count.get() + "vehicles left.");
//			count.decrementAndGet();
//		});
//		repository.saveAll(vehiclesToStore);
		
		logger.info("All vehicle records saved.");
	}

}
