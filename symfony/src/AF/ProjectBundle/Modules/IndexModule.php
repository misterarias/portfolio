<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 18/07/15
 * Time: 00:16
 */

namespace AF\ProjectBundle\Modules;


use AF\Controller\Modules\Module;

class IndexModule extends Module {

	const NAME = "index";

	public function __construct() {
		parent::__construct(IndexModule::NAME, "Home", "af_project");

		$this->setHeaderTitle("Topic modeling historical data")
			->setHeaderSubtitle("Distributed discovery of topics over GDELT data using LDA Spark's MLLib and Elastic Search");
	}
}