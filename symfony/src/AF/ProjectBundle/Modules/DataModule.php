<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 18/07/15
 * Time: 00:16
 */

namespace AF\ProjectBundle\Modules;


use AF\Controller\Modules\Module;

class DataModule extends Module {

	const NAME = "data";

	public function __construct() {
		parent::__construct(DataModule::NAME, "Visualization", "af_project_data");

		$this->setHeaderTitle("The data visualization layer")
			->setHeaderSubtitle("Representation of the topics inferred from the data");
	}
}