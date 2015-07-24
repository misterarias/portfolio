<?php

namespace AF\ProjectBundle\Controller;

use AF\Controller\AFController;
use AF\ProjectBundle\Actors\Actor;
use AF\ProjectBundle\Modules\ConclusionsModule;
use AF\ProjectBundle\Modules\DataModule;
use AF\ProjectBundle\Modules\IndexModule;
use AF\ProjectBundle\Modules\ProcessorModule;
use AF\ProjectBundle\Modules\ProjectModule;

class ProjectController extends AFController {

	public function indexAction() {
		$this->setCurrentModule(IndexModule::NAME);
		$this->setNextModule(ProjectModule::NAME);

		$viewData = array();

		// XXX In a perfect world this info would be in a YAML file
		$me = new Actor(
			"Juan Arias",
			"SCM Tech",
			"I've been working with computer systems for the past eight years, and been learning new things every day. I consider myself a very
			curious person, so getting expertise on Big Data systems was something I had been looking forward for a long time. Looking for the
			 next challenge!",
			"juan-icon.jpg"
		);
		$me->setRole("Senior Engineer")
			->setCompanyUrl("http://www.schibstediberica.es")
			->addSocial("fa-twitter", "https://twitter.com/ariasFreire")
			->addSocial("fa-github", "https://github.com/misterarias/")
			->addSocial("fa-linkedin", "https://www.linkedin.com/profile/view?id=96626628");
		$viewData["juan"] = $me;

		$utad = new Actor(
			"Big Data certificate",
			"U-Tad Madrid",
			"The U-tad's Certificate program in Big Data allows students to lead the design, management and exploitation of next generation infrastructures handling large volumes of data.",
			"utad-icon.png"
		);
		$utad->setCompanyUrl("https://www.u-tad.com/en/estudios/certificate-program-in-big-data/")
			->addSocial("fa-twitter", "https://twitter.com/U_tad")
			->addSocial("fa-facebook", "https://es-es.facebook.com/utadcentrouniversitario")
			->addSocial("fa-linkedin", "http://www.linkedin.com/company/u-tad");
		$viewData["utad"] = $utad;

		return $this->render("@AFProject/Index/main.html.twig", $viewData);
	}

	public function processorAction() {
		$this->setCurrentModule(ProcessorModule::NAME);
		$this->setNextModule(DataModule::NAME);
		$this->setPreviousModule(ProjectModule::NAME);

		return $this->render("@AFProject/Processor/main.html.twig");
	}

	public function dataAction() {
		$this->setCurrentModule(DataModule::NAME);
		$this->setPreviousModule(ProcessorModule::NAME);
		$this->setNextModule(ConclusionsModule::NAME);

		return $this->render("@AFProject/Data/main.html.twig");
	}

	public function projectAction() {
		$this->setCurrentModule(ProjectModule::NAME);
		$this->setNextModule(ProcessorModule::NAME);
		$this->setPreviousModule(IndexModule::NAME);

		return $this->render("@AFProject/Project/main.html.twig");
	}


	public function finalAction() {
		$this->setCurrentModule(ConclusionsModule::NAME);
		$this->setPreviousModule(DataModule::NAME);

		return $this->render("@AFProject/Conclusions/main.html.twig");
	}

	/**
	 * Array of currently enabled modules
	 * @return array
	 */
	function getAllowedModules() {
		return array(
			new IndexModule(),
			new ProjectModule(),
			new ProcessorModule(),
			new DataModule(),
			new ConclusionsModule(),
		);
	}
}
