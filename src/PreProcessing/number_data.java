package PreProcessing;


public class number_data {
	
	private Integer max_number_of_data=1000;
	private double PercentOflabel0=0.7;
	private Integer number_of_data=0;
	private Integer number_of_lebel0=0;
	private Integer number_of_lebel1=0;
	
	public number_data(int entries,double percentOflabel0) {
		this.max_number_of_data=entries;
		this.PercentOflabel0=percentOflabel0;
	}

	public boolean isfull() {
		if (getNumber_of_data()>=this.max_number_of_data)
			return true;
		else
			return false;
	}
	public boolean isfull_lebel0() {
		if ( getNumber_of_lebel0() >= ((double) this.max_number_of_data*this.PercentOflabel0 ) )
			return true;
		else
			return false;
	}
	public boolean isfull_lebel1() {
		if (getNumber_of_lebel1() >=  (double) this.max_number_of_data*(1-this.PercentOflabel0))
			return true;
		else
			return false;
	}
	
	public Integer getMax_number_of_data() {
		return max_number_of_data;
	}
	public Integer getNumber_of_data() {
		return number_of_data;
	}
	public void add_Number_of_data() {
		this.number_of_data++;
	}
	public Integer getNumber_of_lebel1() {
		return number_of_lebel1;
	}
	public void add_Number_of_lebel1() {
		this.add_Number_of_data();
		this.number_of_lebel1++;
	}
	public Integer getNumber_of_lebel0() {
		return number_of_lebel0;
	}
	public void add_Number_of_lebel0() {
		this.add_Number_of_data(); 
		this.number_of_lebel0++;
	}
	public void Initialize(){
		this.number_of_data=0;
		this.number_of_lebel0=0;
		this.number_of_lebel1=0;
	}
 }