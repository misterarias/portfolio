<?php

namespace AF\ProjectBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class DefaultController extends Controller
{
    public function indexAction()
    {
        return $this->render("@AFProject/Default/index.html.twig");
    }
}
