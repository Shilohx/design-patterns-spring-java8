package victor.training.oo.behavioral.strategy;

import static java.util.Arrays.asList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class StrategySpringApp implements CommandLineRunner {
	public static void main(String[] args) {
		new SpringApplicationBuilder(StrategySpringApp.class)
				.profiles("localProps")
				.run(args);
	}


	private ConfigProvider configProvider = new ConfigFileProvider();
	@Autowired
	private CustomsService service;

	// TODO [1] Break CustomsService logic into Strategies
	// TODO [2] Convert it to Chain Of Responsibility
	// TODO [3] Wire with Spring
	// TODO [4] ConfigProvider: selected based on environment props, with Spring
	public void run(String... args) throws Exception {
		System.out.println("Tax for (RO,100,100) = " + service.computeCustomsTax("RO", 100, 100));
		System.out.println("Tax for (CN,100,100) = " + service.computeCustomsTax("CN", 100, 100));
		System.out.println("Tax for (UK,100,100) = " + service.computeCustomsTax("UK", 100, 100));
		
		System.out.println("Property: " + configProvider.getProperties().getProperty("someProp"));
	}
}

@Service
class CustomsService {
	@Autowired
	private TaxCalculatorLocator taxCalculatorLocator;

	public double computeCustomsTax(String originCountry, double tobacoValue, double regularValue) { // UGLY API we CANNOT change
		TaxCalculator a = selectTaxCalculator(originCountry);
		return a.compute(tobacoValue, regularValue);
	}

	private TaxCalculator selectTaxCalculator(String originCountry) {
		return taxCalculatorLocator.locate(isEu(originCountry) ? "EU" :  originCountry);
	}

	private boolean isEu(String originCountry) {
		return asList("FR","ES","RO","NL").contains(originCountry);
	}
}

@Configuration
class TaxCalculatorConfiguration {
	@Bean
	protected ServiceLocatorFactoryBean taxCalculatorLocatorFactoryBean() {
		final ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
		bean.setServiceLocatorInterface(TaxCalculatorLocator.class);
		return bean;
	}
}


interface TaxCalculator {
	double compute(double tobacoValue, double regularValue);
}

interface TaxCalculatorLocator {
	TaxCalculator locate(String type);
}

@Service("CN")
class CNTaxCalculator implements TaxCalculator {
	public double compute(double tobacoValue, double regularValue) {
		return tobacoValue + regularValue;
	}
}

@Service("UK")
class UKTaxCalculator implements TaxCalculator {
	public double compute(double tobacoValue, double regularValue) {
		return tobacoValue/2 + regularValue/2;
	}
}

@Service("EU")
class EUTaxCalculator implements TaxCalculator {
	public double compute(double tobacoValue, double regularValue) {
		return tobacoValue/3;
	}
}
