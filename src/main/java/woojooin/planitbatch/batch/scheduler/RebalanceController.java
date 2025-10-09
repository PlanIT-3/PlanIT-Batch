package woojooin.planitbatch.batch.scheduler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rebalance")
@RequiredArgsConstructor
public class RebalanceController {

	private final RebalanceScheduler rebalanceScheduler;

	@GetMapping("/run")
	public String runRebalanceJob() {
		rebalanceScheduler.runRebalanceJob();

		return "run";
	}
}
