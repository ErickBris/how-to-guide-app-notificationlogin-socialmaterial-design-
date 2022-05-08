<?php
namespace AppBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\FormEvent;
use Symfony\Component\Form\FormEvents;
use Ivory\CKEditorBundle\Form\Type\CKEditorType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;

class StepType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
       $builder
            ->add('title',null,array("label"=>"Step title"))
            ->add('content', CKEditorType::class, 
                    array(
                        "label"=>"Step content",
                        'config_name' => 'step_config'
                    )
                );
        $builder->addEventListener(FormEvents::PRE_SET_DATA, function (FormEvent $event) {
            $article = $event->getData();
            $form = $event->getForm();
            if ($article and null !== $article->getId()) {
                 $form->add("file",null,array("label"=>"","required"=>false));
                 $form->add('save', 'submit',array("label"=>"SAVE STEP"));

            }else{
                 $form->add("file",null,array("label"=>"","required"=>false));
                 $form->add('save', 'submit',array("label"=>"ADD STEP"));

            }
        });

    }
    public function getName()
    {
        return 'Step';
    }
}