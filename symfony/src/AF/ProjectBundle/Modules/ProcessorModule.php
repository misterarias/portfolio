<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 18/07/15
 * Time: 00:16
 */

namespace AF\ProjectBundle\Modules;


use AF\Controller\Modules\Module;

class ProcessorModule extends Module {

	const NAME = "processor";

	public function __construct() {
		parent::__construct(ProcessorModule::NAME, "Processing", "af_project_processor");

		$this->setHeaderTitle("The data processing layer")
			->setHeaderSubtitle("Train LDA over 50000 documents using Spark and MLLib");
	}
}