<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 18/07/15
 * Time: 00:16
 */

namespace AF\ProjectBundle\Modules;


use AF\Controller\Modules\Module;

class ConclusionsModule extends Module {

	const NAME = "final";

	public function __construct() {
		parent::__construct(ConclusionsModule::NAME, "Conclussions", "af_project_final");

		$this->setHeaderTitle("Project conclusions")
			->setHeaderSubtitle("Summary of lessons learned and future steps that would be taken");
	}
}