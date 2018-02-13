<div class="modal fade" id="paymentModal" role="dialog">
    <div class="modal-dialog modal-dialog-centered">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Select a payment method</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body" id="dropinContainer" >
            </div>
            <div class="modal-footer">
            	<button id="submitPaymentButton" type="button" class="btn btn-primary" data-dismiss="modal">Pay</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>

    </div>
</div>


<div class="modal fade" id="processingModal" data-backdrop="static" tabindex="-1" role="dialog" data-keyboard="false">
	<div class="modal-dialog modal-dialog-centered">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
            	<h4 class="modal-title">Processing... Do <span class="text-warning">NOT</span> close your browser.</h4>
            </div>
            <div class="modal-body">
            	<center><i class="fa fa-refresh fa-3x fa-spin" style="color:black"></i></center>
            </div>
        </div>

    </div>
</div>