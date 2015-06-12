<?php

namespace AF\BioBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class DefaultController extends Controller {

    public function indexAction() {
        $availableSections = array();

        $availableSections[] =
            array("name"=>"Service", "template"=>"services.html.twig", "id"=>"services");
        $availableSections[] =
            array("name"=>"Portfolio", "template"=>"portfolio.html.twig", "id"=>"portfolio");
        $availableSections[] =
            array("name"=>"About", "template"=>"about.html.twig", "id"=>"about");
        $availableSections[] =
            array("name"=>"Team", "template"=>"team.html.twig", "id"=>"team");
        $availableSections[] =
            array("name"=>"Contact me", "template"=>"contact.html.twig", "id"=>"contact");


        $viewData = array();
        $viewData["sections"] = $availableSections;
        return $this->render("AFBioBundle:default:index.html.twig", $viewData);
    }
}
