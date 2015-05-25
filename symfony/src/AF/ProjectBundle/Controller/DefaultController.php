<?php

namespace AF\ProjectBundle\Controller;

use AF\Controller\AFController;
use Symfony\Component\HttpFoundation\Response;

class DefaultController extends AFController {

	public function indexAction() {
		return $this->render("@AFProject/Default/index.html.twig");
	}
}
