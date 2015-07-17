<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 18/07/15
 * Time: 00:16
 */

namespace AF\ProjectBundle\Modules;


use AF\Controller\Modules\Module;

class ProjectModule extends Module {

	const NAME = "project";

	public function __construct() {
		parent::__construct(ProjectModule::NAME, "Project structure", "af_project_project");

		$this->setHeaderTitle("Project overview")
			->setHeaderSubtitle("Eagle-eye over the main components of a Big Data problem");
	}
}