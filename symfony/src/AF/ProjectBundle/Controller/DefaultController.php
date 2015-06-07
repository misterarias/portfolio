<?php

namespace AF\ProjectBundle\Controller;

use AF\Controller\AFController;
use Symfony\Component\HttpFoundation\Response;

class DefaultController extends AFController {

	public function indexAction() {
        $this->setCurrentModule("index");
		return $this->render("AFProjectBundle:Index:main.html.twig");
	}

    public function scraperAction() {
        $this->setCurrentModule("scraper");
        return $this->render("AFProjectBundle:Scraper:main.html.twig");
    }

	public function dataAction() {
		$this->setCurrentModule("data");
		return $this->render("AFProjectBundle:Data:main.html.twig");
	}

    /**
     * Array of currently enabled modules
     * @return array
     */
    function getAllowedModules()
    {
        return array("index", "scraper", "data");
    }
}
