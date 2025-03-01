import HW01.Simulation.BasicFirstExample;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyFirstFit;
import org.junit.AfterClass;
import org.junit.Test;


import static org.junit.Assert.*;

public class TestCase1 {
    BasicFirstExample simulation = new BasicFirstExample();
    @Test
    public void compare_cost_and_profit(){
        assertNotEquals(simulation.getCost(),simulation.getCostChargingCustomer());
    }
    @Test
    public void create_cloudlets(){
        simulation.run_simulations();
        assertNotNull(simulation.getCloudletList());
    }
    @Test
    public void distinct_data_centers(){
        simulation.run_simulations();
        assertNotEquals(simulation.getDatacenter0(),simulation.getDatacenter1());
    }
    @Test
    public void VMAllocationPolicyFirstFit(){
        simulation.run_simulations();
        assertEquals(simulation.getDatacenter0VMAllocationPolicy().getClass(), VmAllocationPolicyFirstFit.class);
    }
    @Test
    public void confirm_VMAllocationPolicyBestFit(){
        simulation.run_simulations();
        assertEquals(simulation.getDatacenter0VMAllocationPolicy().getClass(), VmAllocationPolicyFirstFit.class);
    }
    @AfterClass
    public static void simulation_non_empty(){
        BasicFirstExample simulation = new BasicFirstExample();
        assertNotNull(simulation);
    }

}
